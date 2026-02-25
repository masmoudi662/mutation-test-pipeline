import argparse
import csv
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
import time
import traceback
import xml.etree.ElementTree as ET
from datetime import datetime, timezone
from pathlib import Path

from build_patches import (
    PatchReport,
    ensure_maven_settings,
    install_missing_jars,
    patch_red5_base,
    write_patch_report,
)


BASE_DIR = Path(__file__).resolve().parent.parent


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()


def java_file_to_fqcn(path_text: str) -> str:
    normalized = path_text.replace("\\", "/")
    for marker in ("src/test/java/", "src/main/java/"):
        if marker in normalized:
            rel = normalized.split(marker, 1)[1]
            if rel.endswith(".java"):
                rel = rel[:-5]
            return rel.replace("/", ".")
    raise ValueError(f"Could not derive class name from path: {path_text}")


def fqcn_from_source_path(path: Path, source_root: Path) -> str:
    rel = path.relative_to(source_root).as_posix()
    return rel[:-5].replace("/", ".")


def resolve_mapping_path(module_dir: Path, module_name: str, mapped_path: str) -> Path:
    normalized = mapped_path.replace("\\", "/")
    if normalized.startswith(module_name + "/"):
        normalized = normalized[len(module_name) + 1 :]
    return module_dir / normalized


def load_mapping(repo_id: str, mapping_json: str | None) -> tuple[dict, Path]:
    if mapping_json:
        mapping_path = Path(mapping_json)
    else:
        candidates = sorted((BASE_DIR / "classes2test" / "assertion_dataset").glob(f"{repo_id}_*.json"))
        if not candidates:
            raise FileNotFoundError(
                f"No mapping JSON found for repo_id={repo_id} under classes2test/assertion_dataset"
            )
        mapping_path = candidates[0]
    mapping = json.loads(mapping_path.read_text(encoding="utf-8"))
    return mapping, mapping_path


def _is_ai_test_file(path: Path) -> bool:
    name = path.name
    return name.endswith("AiTest.java") or name.endswith("_GeneratedTest.java") or "GeneratedTest" in name


def _restore_disabled_tests(module_dir: Path, report: PatchReport) -> None:
    for disabled_path in sorted(module_dir.rglob("*.java.pipeline_disabled")):
        if not disabled_path.exists():
            continue
        original_path = disabled_path.with_name(disabled_path.name.replace(".pipeline_disabled", ""))
        if original_path.exists():
            continue
        disabled_path.rename(original_path)
        report.changes.append(f"Restored test file: {original_path}")


def _disable_unselected_tests(module_dir: Path, keep_fqcns: set[str], report: PatchReport) -> int:
    src_test_root = module_dir / "src" / "test" / "java"
    if not src_test_root.exists():
        return 0
    disabled = 0
    for test_file in sorted(src_test_root.rglob("*.java")):
        fqcn = fqcn_from_source_path(test_file, src_test_root)
        if fqcn not in keep_fqcns:
            test_file.rename(test_file.with_name(test_file.name + ".pipeline_disabled"))
            disabled += 1
    report.changes.append(f"Disabled {disabled} non-selected test files")
    return disabled


def apply_condition_full(module_dir: Path, condition: str, report: PatchReport) -> list[str]:
    src_test_root = module_dir / "src" / "test" / "java"
    selected: list[str] = []
    if not src_test_root.exists():
        return selected

    _restore_disabled_tests(module_dir, report)
    if condition == "HYBRID":
        for test_file in sorted(src_test_root.rglob("*.java")):
            selected.append(fqcn_from_source_path(test_file, src_test_root))
        report.changes.append("Applied HYBRID condition (no tests disabled)")
        return selected

    disabled_count = 0
    for test_file in sorted(src_test_root.rglob("*.java")):
        is_ai = _is_ai_test_file(test_file)
        disable = (condition == "HUMAN" and is_ai) or (condition == "AI_ONLY" and not is_ai)
        if disable:
            test_file.rename(test_file.with_name(test_file.name + ".pipeline_disabled"))
            disabled_count += 1
        else:
            selected.append(fqcn_from_source_path(test_file, src_test_root))
    report.changes.append(f"Applied {condition} condition, disabled {disabled_count} test files")
    return selected


def resolve_focal_selection(
    module_dir: Path,
    module_name: str,
    mapping: dict,
    condition: str,
) -> tuple[list[str], str]:
    test_root = module_dir / "src" / "test" / "java"
    human_path = resolve_mapping_path(module_dir, module_name, mapping["test_class"]["file"])
    human_fqcn = java_file_to_fqcn(mapping["test_class"]["file"])
    focal_fqcn = java_file_to_fqcn(mapping["focal_class"]["file"])
    focal_identifier = mapping["focal_class"]["identifier"]

    candidates: set[str] = set()
    if test_root.exists():
        for hit in sorted(test_root.rglob(f"{focal_identifier}AiTest.java")):
            candidates.add(fqcn_from_source_path(hit, test_root))
        for hit in sorted(test_root.rglob("*GeneratedTest.java")):
            if focal_identifier in hit.name:
                candidates.add(fqcn_from_source_path(hit, test_root))
    if "." in human_fqcn:
        pkg = human_fqcn.rsplit(".", 1)[0]
        candidates.add(f"{pkg}.{focal_identifier}AiTest")
    if "." in focal_fqcn:
        pkg = focal_fqcn.rsplit(".", 1)[0]
        candidates.add(f"{pkg}.{focal_identifier}AiTest")

    ai_fqcns = []
    for fqcn in sorted(candidates):
        candidate_file = test_root / Path(fqcn.replace(".", "/") + ".java")
        if candidate_file.exists():
            ai_fqcns.append(fqcn)

    if not human_path.exists():
        raise FileNotFoundError(f"Mapped human test class file does not exist: {human_path}")
    if condition in ("AI_ONLY", "HYBRID") and not ai_fqcns:
        raise FileNotFoundError(
            f"No AI test class found for focal class {focal_identifier} in {module_dir / 'src/test/java'}"
        )

    if condition == "HUMAN":
        return [human_fqcn], focal_fqcn
    if condition == "AI_ONLY":
        return ai_fqcns, focal_fqcn
    return sorted(set([human_fqcn] + ai_fqcns)), focal_fqcn


def run_command(
    cmd: list[str],
    cwd: Path,
    report: PatchReport,
    run_status: dict,
    run_dir: Path,
    step_name: str,
) -> subprocess.CompletedProcess:
    report.commands.append(cmd)
    start = time.time()
    proc = subprocess.run(cmd, cwd=str(cwd), text=True, capture_output=True)
    duration = time.time() - start

    log_file = run_dir / f"{len(run_status['commands']):02d}_{step_name}.log"
    log_file.write_text(
        "COMMAND: " + " ".join(cmd) + "\n\nSTDOUT:\n" + proc.stdout + "\nSTDERR:\n" + proc.stderr,
        encoding="utf-8",
    )
    if proc.stdout:
        print(proc.stdout, end="")
    if proc.stderr:
        print(proc.stderr, end="", file=sys.stderr)

    run_status["commands"].append(
        {
            "step": step_name,
            "cmd": cmd,
            "cwd": str(cwd),
            "returncode": proc.returncode,
            "duration_seconds": round(duration, 3),
            "log_file": str(log_file),
        }
    )
    if proc.returncode != 0:
        raise subprocess.CalledProcessError(proc.returncode, cmd, output=proc.stdout, stderr=proc.stderr)
    return proc


def write_json(path: Path, payload: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(payload, indent=2), encoding="utf-8")


def find_mutations_xml(root: Path) -> Path | None:
    hits = sorted(root.glob("**/mutations.xml"), key=lambda p: p.stat().st_mtime, reverse=True)
    for hit in hits:
        if hit.is_file():
            return hit
    return None


def write_mutation_summary(pit_root: Path, out_path: Path) -> None:
    xml_path = find_mutations_xml(pit_root)
    if not xml_path:
        write_json(
            out_path,
            {
                "error": "mutations.xml not found in copied PIT reports",
                "total_mutations": 0,
                "killed_mutations": 0,
                "survived_mutations": 0,
                "mutation_score": 0.0,
            },
        )
        return

    tree = ET.parse(xml_path)
    root = tree.getroot()
    total = 0
    killed = 0
    survived = 0
    for mutation in root.findall(".//mutation"):
        total += 1
        status = (mutation.get("status") or "").upper()
        if status == "KILLED":
            killed += 1
        elif status == "SURVIVED":
            survived += 1

    score = (killed / total) if total else 0.0
    write_json(
        out_path,
        {
            "mutations_xml": str(xml_path),
            "total_mutations": total,
            "killed_mutations": killed,
            "survived_mutations": survived,
            "mutation_score": score,
        },
    )


def append_mutation_summary_csv(
    run_dir: Path,
    repo_id: str,
    module: str,
    condition: str,
    test_scope: str,
    build_tool: str,
    focal_class: str,
    selected_tests: list[str],
    tests_found: str,
) -> None:
    pit_root = run_dir / "pit-reports"
    xml_path = find_mutations_xml(pit_root)
    if not xml_path:
        print(f"[pipeline] warning: mutations.xml not found under {pit_root}; skipping CSV append")
        return
    tree = ET.parse(xml_path)
    root = tree.getroot()

    mutations_generated = 0
    mutations_killed = 0
    mutations_survived = 0
    mutations_no_coverage = 0
    for mutation in root.findall(".//mutation"):
        mutations_generated += 1
        status = (mutation.get("status") or "").upper()
        if status == "KILLED":
            mutations_killed += 1
        elif status == "SURVIVED":
            mutations_survived += 1
        elif status == "NO_COVERAGE":
            mutations_no_coverage += 1

    denom = mutations_killed + mutations_survived
    mutation_score_pct = (mutations_killed / denom * 100.0) if denom else 0.0

    csv_path = BASE_DIR / "artifacts" / "mutation_summary_all_runs.csv"
    csv_path.parent.mkdir(parents=True, exist_ok=True)
    write_header = not csv_path.exists()

    timestamp = run_dir.name.split("_", 1)[0]
    row = [
        timestamp,
        repo_id,
        module,
        condition,
        test_scope,
        build_tool,
        focal_class,
        ",".join(selected_tests),
        tests_found,
        str(mutations_generated),
        str(mutations_killed),
        str(mutations_survived),
        str(mutations_no_coverage),
        f"{mutation_score_pct:.6f}",
        str(run_dir),
    ]
    header = [
        "timestamp",
        "repo_id",
        "module",
        "condition",
        "test_scope",
        "build_tool",
        "focal_class",
        "target_tests",
        "tests_found",
        "mutations_generated",
        "mutations_killed",
        "mutations_survived",
        "mutations_no_coverage",
        "mutation_score_pct",
        "run_dir",
    ]
    with csv_path.open("a", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        if write_header:
            writer.writerow(header)
        writer.writerow(row)
    print("[pipeline] appended mutation summary row to artifacts/mutation_summary_all_runs.csv")


def detect_build_tool(module_dir: Path, build_tool: str) -> str:
    if build_tool in ("maven", "gradle"):
        return build_tool
    if (module_dir / "pom.xml").exists():
        return "maven"
    if (module_dir / "build.gradle").exists() or (module_dir / "build.gradle.kts").exists():
        return "gradle"
    raise RuntimeError(f"Could not auto-detect build tool for module {module_dir}")


def gradle_executable(repo_root: Path, user_gradle_cmd: str | None) -> str:
    if user_gradle_cmd:
        return user_gradle_cmd
    bat = repo_root / "gradlew.bat"
    sh = repo_root / "gradlew"
    if bat.exists():
        return str(bat)
    if sh.exists():
        return str(sh)
    return "gradle"


def gradle_project_path(repo_root: Path, module_dir: Path) -> str:
    rel = module_dir.relative_to(repo_root).as_posix()
    if rel == ".":
        return ":"
    return ":" + rel.replace("/", ":")


def run_compile_and_tests(
    build_tool: str,
    args: argparse.Namespace,
    settings_path: Path,
    repo_root: Path,
    module_dir: Path,
    selected_tests: list[str],
    report: PatchReport,
    run_status: dict,
    run_dir: Path,
) -> None:
    if build_tool == "maven":
        run_command(
            [args.mvn_cmd, "-s", str(settings_path), "-DskipTests", "compile"],
            module_dir,
            report,
            run_status,
            run_dir,
            "compile",
        )
        test_cmd = [args.mvn_cmd, "-s", str(settings_path)]
        if args.test_scope == "focal":
            test_cmd.append(f"-Dtest={','.join(selected_tests)}")
        test_cmd.append("test")
        run_command(test_cmd, module_dir, report, run_status, run_dir, "test")
        return

    gradle_cmd = gradle_executable(repo_root, args.gradle_cmd)
    gradle_path = gradle_project_path(repo_root, module_dir)
    run_command(
        [gradle_cmd, f"{gradle_path}:classes"],
        repo_root,
        report,
        run_status,
        run_dir,
        "compile",
    )
    test_cmd = [gradle_cmd, f"{gradle_path}:test"]
    if args.test_scope == "focal":
        for test_class in selected_tests:
            test_cmd.extend(["--tests", test_class])
    run_command(test_cmd, repo_root, report, run_status, run_dir, "test")


def _read_classpath_file(cp_file: Path) -> list[str]:
    if not cp_file.exists():
        return []
    text = cp_file.read_text(encoding="utf-8").strip()
    if not text:
        return []
    return [entry for entry in text.split(os.pathsep) if entry]


def collect_maven_test_classpath(
    args: argparse.Namespace,
    settings_path: Path,
    module_dir: Path,
    report: PatchReport,
    run_status: dict,
    run_dir: Path,
) -> list[str]:
    cp_file = run_dir / "maven_test_classpath.txt"
    module_classes = str(module_dir / "target" / "classes")
    test_classes = str(module_dir / "target" / "test-classes")
    junit_4132 = str(Path.home() / ".m2" / "repository" / "junit" / "junit" / "4.13.2" / "junit-4.13.2.jar")
    hamcrest_13 = (
        str(Path.home() / ".m2" / "repository" / "org" / "hamcrest" / "hamcrest-core" / "1.3" / "hamcrest-core-1.3.jar")
    )
    cmd = [
        args.mvn_cmd,
        "-s",
        str(settings_path),
        "-DincludeScope=test",
        "-Dmdep.addOutputDirectory=true",
        "-Dmdep.addTestOutputDirectory=true",
        f"-Dmdep.outputFile={cp_file}",
        "dependency:build-classpath",
    ]
    run_command(cmd, module_dir, report, run_status, run_dir, "collect_test_classpath")
    cp_text = cp_file.read_text(encoding="utf-8").strip() if cp_file.exists() else ""
    cp_entries = [entry for entry in cp_text.split(os.pathsep) if entry]
    had_legacy_junit = any(entry.replace("/", "\\").endswith("\\junit\\junit\\4.5\\junit-4.5.jar") for entry in cp_entries)
    if had_legacy_junit:
        if not Path(junit_4132).exists() or not Path(hamcrest_13).exists():
            run_command(
                [args.mvn_cmd, "-s", str(settings_path), "-q", "dependency:get", "-Dartifact=junit:junit:4.13.2"],
                module_dir,
                report,
                run_status,
                run_dir,
                "fetch_junit_4132",
            )
            run_command(
                [args.mvn_cmd, "-s", str(settings_path), "-q", "dependency:get", "-Dartifact=org.hamcrest:hamcrest-core:1.3"],
                module_dir,
                report,
                run_status,
                run_dir,
                "fetch_hamcrest_13",
            )
        if Path(junit_4132).exists() and Path(hamcrest_13).exists():
            cp_entries = [
                entry
                for entry in cp_entries
                if not entry.replace("/", "\\").endswith("\\junit\\junit\\4.5\\junit-4.5.jar")
            ]
            if junit_4132 not in cp_entries:
                cp_entries.append(junit_4132)
            if hamcrest_13 not in cp_entries:
                cp_entries.append(hamcrest_13)
            cp_file.write_text(os.pathsep.join(cp_entries), encoding="utf-8")
            print("[classpath] replaced junit-4.5 with junit-4.13.2 + hamcrest-core-1.3")
        else:
            print("[classpath] warning: junit-4.13.2.jar and/or hamcrest-core-1.3.jar missing; skipping junit rewrite")
    if module_classes not in cp_entries or test_classes not in cp_entries:
        if module_classes not in cp_entries:
            cp_entries.append(module_classes)
        if test_classes not in cp_entries:
            cp_entries.append(test_classes)
        cp_file.write_text(os.pathsep.join(cp_entries), encoding="utf-8")
        print("[classpath] ensured target/classes + target/test-classes are present")
    entries = _read_classpath_file(cp_file)
    entries.extend(
        [
            module_classes,
            test_classes,
        ]
    )
    return sorted(dict.fromkeys(entries))


def collect_gradle_test_classpath(
    args: argparse.Namespace,
    repo_root: Path,
    module_dir: Path,
    report: PatchReport,
    run_status: dict,
    run_dir: Path,
) -> list[str]:
    gradle_cmd = gradle_executable(repo_root, args.gradle_cmd)
    module_path = gradle_project_path(repo_root, module_dir)
    init_script = run_dir / "collect_test_cp.init.gradle"
    init_script.write_text(
        f"""
gradle.afterProject {{ p ->
  if (p.path == "{module_path}") {{
    p.tasks.register("printTestRuntimeClasspath") {{
      doLast {{
        def t = p.tasks.findByName("test")
        if (t != null && t.hasProperty("classpath")) {{
          t.classpath.files.each {{ f -> println("CLASSPATH_ENTRY::" + f.absolutePath) }}
        }}
      }}
    }}
  }}
}}
""".strip(),
        encoding="utf-8",
    )
    proc = run_command(
        [gradle_cmd, "-I", str(init_script), f"{module_path}:printTestRuntimeClasspath", "-q"],
        repo_root,
        report,
        run_status,
        run_dir,
        "collect_test_classpath",
    )
    entries = []
    for line in proc.stdout.splitlines():
        if line.startswith("CLASSPATH_ENTRY::"):
            entries.append(line.split("::", 1)[1].strip())
    entries.extend(
        [
            str(module_dir / "build" / "classes" / "java" / "main"),
            str(module_dir / "build" / "classes" / "java" / "test"),
            str(module_dir / "build" / "resources" / "main"),
            str(module_dir / "build" / "resources" / "test"),
        ]
    )
    return sorted(dict.fromkeys([e for e in entries if e]))


def collect_pit_cli_classpath(
    args: argparse.Namespace,
    settings_path: Path,
    report: PatchReport,
    run_status: dict,
    run_dir: Path,
) -> list[str]:
    temp_dir = Path(tempfile.mkdtemp(prefix="pit_cli_", dir=str(run_dir)))
    pom_path = temp_dir / "pom.xml"
    cp_file = temp_dir / "pit_cli_cp.txt"
    pom_path.write_text(
        f"""
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>tmp</groupId>
  <artifactId>pit-cli-cp</artifactId>
  <version>1.0.0</version>
  <dependencies>
    <dependency>
      <groupId>org.pitest</groupId>
      <artifactId>pitest-command-line</artifactId>
      <version>{args.pit_version}</version>
    </dependency>
  </dependencies>
</project>
""".strip(),
        encoding="utf-8",
    )
    run_command(
        [
            args.mvn_cmd,
            "-s",
            str(settings_path),
            "-f",
            str(pom_path),
            f"-Dmdep.outputFile={cp_file}",
            "dependency:build-classpath",
        ],
        BASE_DIR,
        report,
        run_status,
        run_dir,
        "collect_pit_cli_classpath",
    )
    return _read_classpath_file(cp_file)


def run_pit_cli(
    repo_id: str,
    module: str,
    condition: str,
    test_scope: str,
    build_tool: str,
    args: argparse.Namespace,
    settings_path: Path,
    repo_root: Path,
    module_dir: Path,
    focal_class: str,
    selected_tests: list[str],
    report: PatchReport,
    run_status: dict,
    run_dir: Path,
) -> Path:
    focal_targets = [value.strip() for value in focal_class.split(",") if value.strip()]
    target_classes_arg = ",".join(
        [value if ("*" in value or "?" in value) else f"{value}*" for value in focal_targets]
    )
    expected_class = None
    if focal_targets:
        expected_class = module_dir / "target" / "classes" / Path(*focal_targets[0].split("."))
        expected_class = expected_class.with_suffix(".class")

    if build_tool == "maven":
        sut_classpath = collect_maven_test_classpath(args, settings_path, module_dir, report, run_status, run_dir)
        source_dir = module_dir / "src" / "main" / "java"
    else:
        sut_classpath = collect_gradle_test_classpath(args, repo_root, module_dir, report, run_status, run_dir)
        source_dir = module_dir / "src" / "main" / "java"

    pit_cli_classpath = collect_pit_cli_classpath(args, settings_path, report, run_status, run_dir)
    classpath_entries = pit_cli_classpath + sut_classpath
    classpath_entries = [entry for entry in classpath_entries if entry]
    classpath_joined = os.pathsep.join(classpath_entries)
    pit_classpath_joined = os.pathsep.join(sut_classpath)
    pit_classpath_entries = [entry for entry in pit_classpath_joined.split(os.pathsep) if entry]
    pit_classpath_file = run_dir / "pit_classpath.txt"
    pit_classpath_file.write_text("\n".join(pit_classpath_entries), encoding="utf-8")

    pit_reports_dir = run_dir / "pit-reports"
    if pit_reports_dir.exists():
        shutil.rmtree(pit_reports_dir)

    pit_cmd = [
        "java",
        "-cp",
        classpath_joined,
        "org.pitest.mutationtest.commandline.MutationCoverageReport",
        "--reportDir",
        str(pit_reports_dir),
        "--targetClasses",
        target_classes_arg,
        "--targetTests",
        ",".join(selected_tests),
        "--sourceDirs",
        str(source_dir),
        "--mutableCodePaths",
        str(module_dir / "target" / "classes"),
        "--classPathFile",
        str(pit_classpath_file),
        "--includeLaunchClasspath",
        "false",
        "--outputFormats",
        "XML,HTML",
    ]
    print(
        f"[pit] targetClasses={target_classes_arg} targetTests={','.join(selected_tests)} "
        f"classExists={expected_class.exists() if expected_class else False}"
    )
    print(f"[pit] classPathFile={pit_classpath_file} entries={len(pit_classpath_entries)}")
    if "org.red5.server.service.ConversionUtils" in [value.strip() for value in focal_class.split(",")]:
        required_class = (
            module_dir / "target" / "classes" / "org" / "red5" / "server" / "service" / "ConversionUtils.class"
        )
        if not required_class.exists():
            raise RuntimeError(f"Required compiled class missing before PIT: {required_class}")
    pit_proc = run_command(pit_cmd, module_dir, report, run_status, run_dir, "pit")
    tests_found = ""
    tests_found_match = re.search(r"Found\s+(\d+)\s+tests", f"{pit_proc.stdout}\n{pit_proc.stderr}")
    if tests_found_match:
        tests_found = tests_found_match.group(1)
    append_mutation_summary_csv(
        run_dir=run_dir,
        repo_id=repo_id,
        module=module,
        condition=condition,
        test_scope=test_scope,
        build_tool=build_tool,
        focal_class=focal_class,
        selected_tests=selected_tests,
        tests_found=tests_found,
    )
    return pit_reports_dir


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Run deterministic test + PIT pipeline across Maven/Gradle."
    )
    parser.add_argument("--repo-id", required=True, help="Target repository id, e.g. 103035")
    parser.add_argument("--module", required=True, help="Module directory inside target-repos/<repo-id>.")
    parser.add_argument(
        "--condition",
        default="HUMAN",
        choices=["HUMAN", "AI_ONLY", "HYBRID"],
        help="Test condition to run.",
    )
    parser.add_argument(
        "--test-scope",
        default="full",
        choices=["full", "focal"],
        help="Run full suite or only focal mapped tests.",
    )
    parser.add_argument(
        "--mapping-json",
        default=None,
        help="Path to mapping JSON (defaults to first classes2test/assertion_dataset/<repo_id>_*.json).",
    )
    parser.add_argument("--run-pit", action="store_true", help="Also run PIT after tests.")
    parser.add_argument(
        "--build-tool",
        default="auto",
        choices=["auto", "maven", "gradle"],
        help="Build tool selection.",
    )
    parser.add_argument("--mvn-cmd", default="mvn.cmd", help="Maven executable name/path.")
    parser.add_argument("--gradle-cmd", default=None, help="Gradle executable name/path.")
    parser.add_argument("--pit-version", default="1.6.9", help="PIT CLI version for pitest-command-line.")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    repo_root = BASE_DIR / "target-repos" / args.repo_id
    module_dir = repo_root / args.module
    if not module_dir.exists():
        raise SystemExit(f"Module directory does not exist: {module_dir}")

    build_tool = detect_build_tool(module_dir, args.build_tool)
    run_id = (
        f"{datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%SZ')}_"
        f"{args.repo_id}_{args.module}_{args.condition}_{args.test_scope}_{build_tool}"
    )
    run_dir = BASE_DIR / "artifacts" / "runs" / run_id
    run_dir.mkdir(parents=True, exist_ok=True)

    run_status = {
        "run_id": run_id,
        "repo_id": args.repo_id,
        "module": args.module,
        "condition": args.condition,
        "test_scope": args.test_scope,
        "build_tool": build_tool,
        "run_pit": args.run_pit,
        "started_at_utc": utc_now(),
        "ended_at_utc": None,
        "success": False,
        "commands": [],
        "selected_test_classes": [],
        "target_classes": [],
        "mapping_json": None,
        "error": None,
    }

    settings_path = ensure_maven_settings(BASE_DIR)
    report = patch_red5_base(repo_root) if args.repo_id == "103035" else PatchReport(
        repo_id=args.repo_id,
        repo_root=str(repo_root),
        module_dir=str(module_dir),
    )
    report.settings_path = str(settings_path)
    report.changes.append("Ensured Maven settings.xml with HTTPS mirrors")
    report.changes.append(f"Using build tool: {build_tool}")

    report_path = BASE_DIR / "artifacts" / "build_patches" / f"{args.repo_id}_{args.module}.json"

    mapping = None
    selected_tests: list[str] = []
    focal_class = None
    pit_reports_dir = run_dir / "pit-reports"

    try:
        if args.test_scope == "focal" or args.run_pit:
            mapping, mapping_path = load_mapping(args.repo_id, args.mapping_json)
            run_status["mapping_json"] = str(mapping_path)

        _restore_disabled_tests(module_dir, report)
        if args.test_scope == "full":
            selected_tests = apply_condition_full(module_dir, args.condition, report)
            keep_fqcns = set(selected_tests)
        else:
            if mapping is None:
                raise RuntimeError("Focal test scope requires a mapping JSON.")
            selected_tests, focal_class = resolve_focal_selection(module_dir, args.module, mapping, args.condition)
            keep_fqcns = set(selected_tests)
            _disable_unselected_tests(module_dir, keep_fqcns, report)
            report.changes.append(f"Applied focal test scope with selected tests: {selected_tests}")

        run_status["selected_test_classes"] = selected_tests

        # Existing reproducibility patches apply to Maven repos; keep for compatibility.
        if build_tool == "maven":
            install_missing_jars(BASE_DIR, settings_path, report, mvn_cmd=args.mvn_cmd)

        run_compile_and_tests(
            build_tool,
            args,
            settings_path,
            repo_root,
            module_dir,
            selected_tests,
            report,
            run_status,
            run_dir,
        )

        if args.run_pit:
            if mapping is None:
                raise RuntimeError("PIT execution requires mapping JSON for focal target classes/tests.")
            if not focal_class:
                focal_class = java_file_to_fqcn(mapping["focal_class"]["file"])
            run_status["target_classes"] = [focal_class]
            pit_reports_dir = run_pit_cli(
                args.repo_id,
                args.module,
                args.condition,
                args.test_scope,
                build_tool,
                args,
                settings_path,
                repo_root,
                module_dir,
                focal_class,
                selected_tests,
                report,
                run_status,
                run_dir,
            )

        run_status["success"] = True
        report.changes.append(f"Run artifacts written to {run_dir}")
    except Exception as exc:  # noqa: BLE001
        run_status["error"] = str(exc)
        report.changes.append(f"Pipeline failed: {exc}")
        report.changes.append(traceback.format_exc())
        raise
    finally:
        run_status["ended_at_utc"] = utc_now()
        if args.run_pit:
            write_mutation_summary(pit_reports_dir, run_dir / "mutation_summary.json")
        _restore_disabled_tests(module_dir, report)
        write_json(run_dir / "run_status.json", run_status)
        write_patch_report(report, report_path)

    print(f"Wrote patch report: {report_path}")
    print(f"Wrote run artifacts: {run_dir}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
