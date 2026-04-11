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
    write_patch_report,
)


BASE_DIR = Path(__file__).resolve().parent.parent
GENERATED_TESTS_DIR = BASE_DIR / "experiments" / "generated_tests"


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
        candidates = sorted((BASE_DIR / "classes2test" / "classes2test-main" / "dataset").glob(f"{repo_id}_*.json"))
        if not candidates:
            raise FileNotFoundError(
                f"No mapping JSON found for repo_id={repo_id} under classes2test/assertion_dataset"
            )
        mapping_path = candidates[0]
    mapping = json.loads(mapping_path.read_text(encoding="utf-8"))
    return mapping, mapping_path



def expected_ai_test_path(module_dir: Path, mapping: dict) -> Path:
    focal_file = mapping["focal_class"]["file"]
    focal_fqcn = java_file_to_fqcn(focal_file)
    focal_identifier = mapping["focal_class"]["identifier"]
    pkg = focal_fqcn.rsplit(".", 1)[0] if "." in focal_fqcn else ""
    rel_dir = Path(*pkg.split(".")) if pkg else Path()
    return module_dir / "src" / "test" / "java" / rel_dir / f"{focal_identifier}AiTest.java"


def provision_ai_test_if_missing(
    repo_id: str,
    module: str,
    module_dir: Path,
    mapping: dict,
    mapping_path: Path,
    report: PatchReport,
) -> tuple[bool, str]:
    target_path = expected_ai_test_path(module_dir, mapping)
    if target_path.exists():
        return True, f"AI test already exists: {target_path}"

    generated_source = GENERATED_TESTS_DIR / f"{mapping_path.stem}_GeneratedTest.java"
    if not generated_source.exists():
        return False, f"Generated AI test not found: {generated_source}"

    raw = generated_source.read_text(encoding="utf-8")
    lines = raw.splitlines()
    if lines and lines[0].strip().lower() == "java":
        lines = lines[1:]
    text = "\n".join(lines)

    focal_fqcn = java_file_to_fqcn(mapping["focal_class"]["file"])
    expected_pkg = focal_fqcn.rsplit(".", 1)[0] if "." in focal_fqcn else ""
    if expected_pkg:
        pkg_decl = re.search(r"^\s*package\s+([a-zA-Z0-9_.]+)\s*;\s*$", text, flags=re.MULTILINE)
        if pkg_decl:
            text = re.sub(
                r"^\s*package\s+[a-zA-Z0-9_.]+\s*;\s*$",
                f"package {expected_pkg};",
                text,
                count=1,
                flags=re.MULTILINE,
            )
        else:
            text = f"package {expected_pkg};\n\n{text}"

    class_name = f"{mapping['focal_class']['identifier']}AiTest"
    class_match = re.search(r"\bpublic\s+class\s+([A-Za-z_][A-Za-z0-9_]*)\b", text)
    if not class_match:
        return False, f"Generated AI test has no public class declaration: {generated_source}"

    old_class_name = class_match.group(1)
    text = re.sub(
        r"\bpublic\s+class\s+[A-Za-z_][A-Za-z0-9_]*\b",
        f"public class {class_name}",
        text,
        count=1,
    )
    text = re.sub(rf"\bnew\s+{re.escape(old_class_name)}\s*\(", f"new {class_name}(", text)

    target_path.parent.mkdir(parents=True, exist_ok=True)
    target_path.write_text(text, encoding="utf-8")
    report.changes.append(f"Injected AI test for {repo_id}/{module}: {generated_source} -> {target_path}")
    return True, f"Injected AI test to {target_path}"
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


def flatten_message(text: str, max_len: int = 400) -> str:
    single = " ".join((text or "").split())
    return single[:max_len]


def classify_error(exc: BaseException) -> str:
    message = str(exc)
    haystack = f"{type(exc).__name__}\n{message}".lower()
    if isinstance(exc, subprocess.CalledProcessError):
        haystack = f"{haystack}\n{(exc.stderr or '').lower()}\n{(exc.output or '').lower()}"

    if "no ai test class found" in haystack:
        return "FILE_NOT_FOUND"
    if "module directory does not exist" in haystack:
        return "MODULE_MISSING"
    if (
        "timed out" in haystack
        or "timeoutexpired" in haystack
        or isinstance(exc, subprocess.TimeoutExpired)
    ):
        return "TIMEOUT"
    if (
        "maven-default-http-blocker" in haystack
        or "blocked mirror for repositories" in haystack
        or "blocked mirror" in haystack
    ):
        return "HTTP_BLOCKER"
    if (
        "was not found in" in haystack
        or "failure to find" in haystack
        or "cached in the local repository" in haystack
    ):
        return "SNAPSHOT_MISSING"
    if (
        "dependencyresolutionexception" in haystack
        or "could not resolve dependencies" in haystack
        or "could not find artifact" in haystack
    ):
        return "DEP_RESOLUTION"
    if (
        "compilation failure" in haystack
        or "compilation error" in haystack
        or "failed to execute goal org.apache.maven.plugins:maven-compiler-plugin" in haystack
    ):
        return "COMPILATION"
    if (
        "surefirebooterforkexception" in haystack
        or "there are test failures" in haystack
        or "failed tests:" in haystack
        or "tests run:" in haystack
    ):
        return "TEST_FAILURE"
    if (
        "org.pitest" in haystack
        or "mutationcoveragereport" in haystack
        or "pitest" in haystack
    ):
        return "PIT_ERROR"
    if isinstance(exc, FileNotFoundError):
        return "FILE_NOT_FOUND"
    return "OTHER"


def maven_prefix(args: argparse.Namespace, settings_path: Path) -> list[str]:
    cmd = [args.mvn_cmd]
    if args.mvn_force_update:
        cmd.append("-U")
    cmd.extend(["-s", str(settings_path)])
    return cmd


def extract_mutation_metrics(run_dir: Path) -> dict[str, str]:
    pit_root = run_dir / "pit-reports"
    xml_path = find_mutations_xml(pit_root)
    if not xml_path:
        return {
            "tests_found": "",
            "mutations_generated": "",
            "mutations_killed": "",
            "mutations_survived": "",
            "mutations_no_coverage": "",
            "mutation_score_pct": "",
        }

    tree = ET.parse(xml_path)
    root = tree.getroot()
    mutations_generated = 0
    mutations_killed = 0
    mutations_survived = 0
    mutations_no_coverage = 0
    tests_found = ""

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
    return {
        "tests_found": tests_found,
        "mutations_generated": str(mutations_generated),
        "mutations_killed": str(mutations_killed),
        "mutations_survived": str(mutations_survived),
        "mutations_no_coverage": str(mutations_no_coverage),
        "mutation_score_pct": f"{mutation_score_pct:.6f}",
    }


def write_final_comparison_latest(csv_path: Path) -> None:
    if not csv_path.exists():
        return
    with csv_path.open(newline="", encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    if not rows:
        return

    latest_success_by_key: dict[tuple[str, str, str, str, str], dict[str, str]] = {}
    for row in rows:
        condition = row.get("condition", "")
        status = row.get("status", "")
        if condition not in ("HUMAN", "AI_ONLY"):
            continue
        if status != "OK":
            continue
        key = (
            row.get("repo_id", ""),
            row.get("module", ""),
            row.get("test_scope", ""),
            row.get("build_tool", ""),
            condition,
        )
        prev = latest_success_by_key.get(key)
        if not prev or row.get("timestamp", "") >= prev.get("timestamp", ""):
            latest_success_by_key[key] = row

    grouped: dict[tuple[str, str, str, str], dict[str, dict[str, str]]] = {}
    for (repo_id, module, test_scope, build_tool, condition), row in latest_success_by_key.items():
        pair_key = (repo_id, module, test_scope, build_tool)
        grouped.setdefault(pair_key, {})[condition] = row

    out_path = BASE_DIR / "experiments" / "RQ1_mutation_score" / "results" / "final_comparison_latest.csv"
    out_path.parent.mkdir(parents=True, exist_ok=True)
    header = [
        "repo_id",
        "module",
        "test_scope",
        "build_tool",
        "human_score_pct",
        "ai_score_pct",
        "delta",
        "human_timestamp",
        "ai_only_timestamp",
        "human_run_dir",
        "ai_only_run_dir",
        "human_mutations_generated",
        "ai_only_mutations_generated",
        "human_mutations_killed",
        "ai_only_mutations_killed",
        "human_mutations_survived",
        "ai_only_mutations_survived",
        "human_error_type",
        "ai_only_error_type",
    ]
    with out_path.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(header)
        for pair_key in sorted(grouped.keys()):
            pair = grouped[pair_key]
            if "HUMAN" not in pair or "AI_ONLY" not in pair:
                continue
            human = pair["HUMAN"]
            ai = pair["AI_ONLY"]
            human_score = float(human.get("mutation_score_pct") or "0")
            ai_score = float(ai.get("mutation_score_pct") or "0")
            writer.writerow(
                [
                    pair_key[0],
                    pair_key[1],
                    pair_key[2],
                    pair_key[3],
                    human.get("mutation_score_pct", ""),
                    ai.get("mutation_score_pct", ""),
                    f"{(ai_score - human_score):.6f}",
                    human.get("timestamp", ""),
                    ai.get("timestamp", ""),
                    human.get("run_dir", ""),
                    ai.get("run_dir", ""),
                    human.get("mutations_generated", ""),
                    ai.get("mutations_generated", ""),
                    human.get("mutations_killed", ""),
                    ai.get("mutations_killed", ""),
                    human.get("mutations_survived", ""),
                    ai.get("mutations_survived", ""),
                    human.get("error_type", ""),
                    ai.get("error_type", ""),
                ]
            )


def upgrade_mutation_summary_csv_schema(csv_path: Path, header: list[str]) -> None:
    if not csv_path.exists():
        return
    with csv_path.open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        fieldnames = reader.fieldnames or []
        if fieldnames == header:
            return
        existing_rows = list(reader)

    with csv_path.open("w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=header)
        writer.writeheader()
        for old_row in existing_rows:
            normalized = {column: old_row.get(column, "") for column in header}
            if "status" not in fieldnames:
                normalized["status"] = "OK" if (old_row.get("mutation_score_pct") or "").strip() else ""
                normalized["phase"] = "summarize" if normalized["status"] == "OK" else ""
                normalized["exit_code"] = "0" if normalized["status"] == "OK" else ""
                normalized["error_type"] = ""
                normalized["error_message"] = ""
            if "retries" not in fieldnames:
                normalized["retries"] = "0"
            if "fixed_by" not in fieldnames:
                normalized["fixed_by"] = "NONE"
            writer.writerow(normalized)


def append_mutation_summary_csv(
    run_status: dict,
    run_dir: Path,
) -> None:
    metrics = extract_mutation_metrics(run_dir)
    csv_path = BASE_DIR / "experiments" / "RQ1_mutation_score" / "results" / "mutation_summary_all_runs.csv"
    csv_path.parent.mkdir(parents=True, exist_ok=True)
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
        "status",
        "phase",
        "exit_code",
        "error_type",
        "error_message",
        "retries",
        "fixed_by",
        "pit_target_classes",
        "pit_version",
        "pit_status",
        "pit_log_file",
        "run_dir",
    ]
    upgrade_mutation_summary_csv_schema(csv_path, header)

    timestamp = run_dir.name.split("_", 1)[0]
    row = [
        timestamp,
        run_status.get("repo_id", ""),
        run_status.get("module", ""),
        run_status.get("condition", ""),
        run_status.get("test_scope", ""),
        run_status.get("build_tool", ""),
        run_status.get("focal_class", ""),
        ",".join(run_status.get("selected_test_classes", [])),
        run_status.get("tests_found", "") or metrics["tests_found"],
        metrics["mutations_generated"],
        metrics["mutations_killed"],
        metrics["mutations_survived"],
        metrics["mutations_no_coverage"],
        metrics["mutation_score_pct"],
        run_status.get("status", ""),
        run_status.get("phase", ""),
        str(run_status.get("exit_code", "")),
        run_status.get("error_type", ""),
        run_status.get("error_message", ""),
        str(run_status.get("retries", 0)),
        run_status.get("fixed_by", "NONE"),
        run_status.get("pit_target_classes", ""),
        run_status.get("pit_version", ""),
        run_status.get("pit_status", ""),
        run_status.get("pit_log_file", ""),
        str(run_dir),
    ]
    with csv_path.open("a", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        if csv_path.stat().st_size == 0:
            writer.writerow(header)
        writer.writerow(row)
    write_final_comparison_latest(csv_path)
    print("[pipeline] appended mutation summary row to experiments/RQ1_mutation_score/results/mutation_summary_all_runs.csv")


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
        run_status["phase"] = "compile"
        run_command(
            maven_prefix(args, settings_path) + ["-DskipTests", "compile"],
            module_dir,
            report,
            run_status,
            run_dir,
            "compile",
        )
        run_status["phase"] = "test"
        test_cmd = maven_prefix(args, settings_path)
        if args.test_scope == "focal":
            test_cmd.append(f"-Dtest={','.join(selected_tests)}")
        test_cmd.append("test")
        run_command(test_cmd, module_dir, report, run_status, run_dir, "test")
        return

    gradle_cmd = gradle_executable(repo_root, args.gradle_cmd)
    gradle_path = gradle_project_path(repo_root, module_dir)
    run_status["phase"] = "compile"
    run_command(
        [gradle_cmd, f"{gradle_path}:classes"],
        repo_root,
        report,
        run_status,
        run_dir,
        "compile",
    )
    run_status["phase"] = "test"
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
        *maven_prefix(args, settings_path),
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
                maven_prefix(args, settings_path) + ["-q", "dependency:get", "-Dartifact=junit:junit:4.13.2"],
                module_dir,
                report,
                run_status,
                run_dir,
                "fetch_junit_4132",
            )
            run_command(
                maven_prefix(args, settings_path) + ["-q", "dependency:get", "-Dartifact=org.hamcrest:hamcrest-core:1.3"],
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
            *maven_prefix(args, settings_path),
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
    run_status["pit_target_classes"] = target_classes_arg
    run_status["pit_version"] = args.pit_version
    run_status["pit_config"] = {
        "pit_version": args.pit_version,
        "target_classes": target_classes_arg,
        "target_tests": ",".join(selected_tests),
        "source_dir": str(source_dir),
        "mutable_code_paths": str(module_dir / "target" / "classes"),
        "class_path_file": str(pit_classpath_file),
    }
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
    run_status["tests_found"] = tests_found
    run_status["pit_status"] = "OK"
    if run_status.get("commands"):
        run_status["pit_log_file"] = run_status["commands"][-1].get("log_file", "")
    return pit_reports_dir



def ensure_repo_settings_https(repo_id: str) -> Path:
    repo_root = BASE_DIR / "target-repos" / repo_id
    mvn_dir = repo_root / ".mvn"
    mvn_dir.mkdir(parents=True, exist_ok=True)
    settings_path = mvn_dir / "settings.xml"
    settings_path.write_text(
        """<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>central-https</id>
      <name>Maven Central HTTPS</name>
      <url>https://repo1.maven.org/maven2</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
    <mirror>
      <id>oss-sonatype-https</id>
      <mirrorOf>appfuse-snapshots</mirrorOf>
      <name>Force HTTPS for oss.sonatype snapshots</name>
      <url>https://oss.sonatype.org/content/repositories/appfuse-snapshots</url>
    </mirror>
  </mirrors>
  <profiles>
    <profile>
      <id>allow-https-repos</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>false</enabled></snapshots>
        </repository>
        <repository>
          <id>apache-snapshots</id>
          <url>https://repository.apache.org/snapshots</url>
          <releases><enabled>false</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
        <repository>
          <id>sonatype-snapshots</id>
          <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
          <releases><enabled>false</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>false</enabled></snapshots>
        </pluginRepository>
        <pluginRepository>
          <id>apache-snapshots</id>
          <url>https://repository.apache.org/snapshots</url>
          <releases><enabled>false</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </pluginRepository>
        <pluginRepository>
          <id>sonatype-snapshots</id>
          <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
          <releases><enabled>false</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>allow-https-repos</activeProfile>
  </activeProfiles>
</settings>
""",
        encoding="utf-8",
    )
    return settings_path


def clear_http_blocker_cache(error_message: str) -> int:
    m2_repo = Path.home() / ".m2" / "repository"
    if not m2_repo.exists():
        return 0

    groups: set[str] = set()
    for group_id, _artifact_id in re.findall(r"([A-Za-z0-9_.-]+):([A-Za-z0-9_.-]+):", error_message or ""):
        if "." in group_id:
            groups.add(group_id)
    if "org.appfuse:" in (error_message or ""):
        groups.add("org.appfuse")

    removed = 0
    for group_id in sorted(groups):
        target = m2_repo / Path(*group_id.split("."))
        if not target.exists():
            continue
        try:
            shutil.rmtree(target)
            removed += 1
        except OSError as exc:
            print(f"[warn] failed deleting cache path {target}: {exc}")
    return removed


def determine_retry_strategy(error_type: str, error_message: str) -> str:
    hay = f"{error_type}\n{error_message}".lower()
    if (
        "http_blocker" in hay
        or "maven-default-http-blocker" in hay
        or "blocked mirror" in hay
        or "appfuse-snapshots" in hay
        or "oss.sonatype.org" in hay
    ):
        return "HTTP_SETTINGS_FIX"
    return "NONE"


def write_per_repo_condition_summary(run_status: dict, run_dir: Path) -> None:
    repo_id = run_status.get("repo_id", "")
    module = run_status.get("module", "")
    condition = run_status.get("condition", "")
    if not repo_id or not module or not condition:
        return
    module_path = Path(*[part for part in module.replace("\\", "/").split("/") if part])
    out_path = BASE_DIR / "experiments" / "RQ1_mutation_score" / "per_repo" / repo_id / module_path / condition / "mutation_summary.json"
    metrics = extract_mutation_metrics(run_dir)
    payload = {
        "repo_id": repo_id,
        "module": module,
        "condition": condition,
        "status": run_status.get("status", "FAIL"),
        "error_type": run_status.get("error_type", ""),
        "error_message": run_status.get("error_message", ""),
        "metrics": {
            "mutation_score_pct": metrics.get("mutation_score_pct", ""),
            "mutants_total": metrics.get("mutations_generated", ""),
            "killed": metrics.get("mutations_killed", ""),
            "survived": metrics.get("mutations_survived", ""),
        },
        "pit_config": run_status.get("pit_config", {}),
        "fixed_by": run_status.get("fixed_by", "NONE"),
        "settings_path": run_status.get("settings_path", ""),
        "settings_copy": run_status.get("settings_copy", ""),
        "run_dir": str(run_dir),
    }
    write_json(out_path, payload)


def copy_settings_for_attempt(settings_path: Path, run_dir: Path, attempt: int) -> str:
    if not settings_path.exists():
        return ""
    out = run_dir / f"settings_used_attempt{attempt}.xml"
    shutil.copy2(settings_path, out)
    return str(out)
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
        help="Path to mapping JSON (defaults to first classes2test/classes2test-main/dataset/<repo_id>_*.json).",
    )
    parser.add_argument("--run-pit", action="store_true", help="Also run PIT after tests.")
    parser.add_argument(
        "--build-tool",
        default="auto",
        choices=["auto", "maven", "gradle"],
        help="Build tool selection.",
    )
    parser.add_argument("--mvn-cmd", default="mvn.cmd", help="Maven executable name/path.")
    parser.add_argument("--mvn-force-update", action="store_true", help="Pass -U to Maven commands.")
    parser.add_argument(
        "--settings-override",
        default=None,
        help="Optional path to Maven settings.xml to use instead of the default generated settings.",
    )
    parser.add_argument("--gradle-cmd", default=None, help="Gradle executable name/path.")
    parser.add_argument("--pit-version", default="1.6.9", help="PIT CLI version for pitest-command-line.")
    parser.add_argument("--retries", type=int, default=0, help="Retry count metadata for this run.")
    parser.add_argument("--fixed-by", default="NONE", help="Applied fix label metadata for this run.")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    repo_root = BASE_DIR / "target-repos" / args.repo_id
    module_dir = repo_root / args.module
    build_tool = args.build_tool if args.build_tool != "auto" else "auto"
    run_id = (
        f"{datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%SZ')}_"
        f"{args.repo_id}_{args.module}_{args.condition}_{args.test_scope}_{build_tool}"
    )
    run_dir = BASE_DIR / "logs" / "runs" / run_id
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
        "status": "FAIL",
        "phase": "clone",
        "exit_code": 1,
        "error_type": "",
        "error_message": "",
        "retries": 0,
        "fixed_by": args.fixed_by,
        "commands": [],
        "selected_test_classes": [],
        "target_classes": [],
        "focal_class": "",
        "tests_found": "",
        "pit_target_classes": "",
        "pit_version": args.pit_version,
        "pit_status": "",
        "pit_log_file": "",
        "pit_config": {},
        "settings_path": "",
        "settings_copy": "",
        "mapping_json": None,
        "error": None,
    }

    base_settings = Path(args.settings_override) if args.settings_override else ensure_maven_settings(BASE_DIR)
    current_settings = base_settings
    current_fixed_by = args.fixed_by

    report = PatchReport(
        repo_id=args.repo_id,
        repo_root=str(repo_root),
        module_dir=str(module_dir),
    )
    report_path = BASE_DIR / "logs" / "build_patches" / f"{args.repo_id}_{args.module}.json"

    mapping = None
    mapping_path: Path | None = None
    selected_tests: list[str] = []
    focal_class = None
    pit_reports_dir = run_dir / "pit-reports"

    try:
        run_status["phase"] = "clone"
        if not module_dir.exists():
            raise FileNotFoundError(f"Module directory does not exist: {module_dir}")
        build_tool = detect_build_tool(module_dir, args.build_tool)
        run_status["build_tool"] = build_tool
        report.changes.append(f"Using build tool: {build_tool}")

        if args.test_scope == "focal" or args.run_pit:
            mapping, mapping_path = load_mapping(args.repo_id, args.mapping_json)
            run_status["mapping_json"] = str(mapping_path)
            run_status["focal_class"] = java_file_to_fqcn(mapping["focal_class"]["file"])

        if args.condition == "AI_ONLY" and mapping is not None and mapping_path is not None:
            ok, msg = provision_ai_test_if_missing(args.repo_id, args.module, module_dir, mapping, mapping_path, report)
            if ok:
                print(f"[ai-test] {msg}")
            else:
                raise FileNotFoundError(msg)

        _restore_disabled_tests(module_dir, report)
        if args.test_scope == "full":
            selected_tests = apply_condition_full(module_dir, args.condition, report)
        else:
            if mapping is None:
                raise RuntimeError("Focal test scope requires a mapping JSON.")
            selected_tests, focal_class = resolve_focal_selection(module_dir, args.module, mapping, args.condition)
            _disable_unselected_tests(module_dir, set(selected_tests), report)
            report.changes.append(f"Applied focal test scope with selected tests: {selected_tests}")
            run_status["focal_class"] = focal_class or run_status["focal_class"]

        run_status["selected_test_classes"] = selected_tests

        if build_tool == "maven":
            install_missing_jars(BASE_DIR, current_settings, report, mvn_cmd=args.mvn_cmd)

        max_retries = max(0, int(args.retries))
        attempt = 0
        while True:
            run_status["retries"] = attempt
            run_status["fixed_by"] = current_fixed_by
            run_status["settings_path"] = str(current_settings)
            report.settings_path = str(current_settings)
            try:
                run_status["settings_copy"] = copy_settings_for_attempt(current_settings, run_dir, attempt + 1)
                if run_status["settings_copy"]:
                    print(f"[attempt {attempt + 1}] settings={run_status['settings_copy']} fixed_by={current_fixed_by}")
                    report.changes.append(
                        f"Attempt {attempt + 1}: settings={run_status['settings_copy']} fixed_by={current_fixed_by}"
                    )
            except OSError as exc:
                print(f"[warn] could not copy settings for attempt {attempt + 1}: {exc}")

            args.mvn_force_update = bool(args.mvn_force_update or current_fixed_by == "HTTP_SETTINGS_FIX")

            try:
                run_compile_and_tests(
                    build_tool,
                    args,
                    current_settings,
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
                    run_status["focal_class"] = focal_class
                    run_status["phase"] = "pit"
                    pit_reports_dir = run_pit_cli(
                        args.repo_id,
                        args.module,
                        args.condition,
                        args.test_scope,
                        build_tool,
                        args,
                        current_settings,
                        repo_root,
                        module_dir,
                        focal_class,
                        selected_tests,
                        report,
                        run_status,
                        run_dir,
                    )

                run_status["success"] = True
                run_status["status"] = "OK"
                run_status["exit_code"] = 0
                run_status["error"] = None
                run_status["error_type"] = ""
                run_status["error_message"] = ""
                report.changes.append(f"Run artifacts written to {run_dir}")
                break
            except subprocess.CalledProcessError as exc:
                detailed_message = str(exc)
                detail = exc.stderr or exc.output or ""
                if detail:
                    detailed_message = f"{detailed_message}: {detail}"
                run_status["error"] = detailed_message
                run_status["status"] = "FAIL"
                run_status["exit_code"] = int(exc.returncode or 1)
                run_status["error_type"] = classify_error(exc)
                run_status["error_message"] = flatten_message(detailed_message)
                if run_status.get("phase") == "pit":
                    run_status["pit_status"] = "FAIL"
                    if run_status.get("commands"):
                        run_status["pit_log_file"] = run_status["commands"][-1].get("log_file", "")

                strategy = determine_retry_strategy(run_status["error_type"], detailed_message)
                can_retry = attempt < max_retries and strategy == "HTTP_SETTINGS_FIX"
                if can_retry:
                    removed = clear_http_blocker_cache(detailed_message)
                    if removed:
                        print(f"[retry] cleared {removed} HTTP-blocked cache group paths")
                    current_settings = ensure_repo_settings_https(args.repo_id)
                    current_fixed_by = "HTTP_SETTINGS_FIX"
                    args.mvn_force_update = True
                    attempt += 1
                    print(
                        f"[retry] attempt={attempt + 1} fixed_by=HTTP_SETTINGS_FIX "
                        f"settings={current_settings} mvn_force_update=True"
                    )
                    report.changes.append(
                        f"Retrying with HTTP_SETTINGS_FIX; settings={current_settings}; mvn_force_update=True"
                    )
                    continue

                report.changes.append(
                    f"Pipeline attempt failed (no further retry): phase={run_status.get('phase')} "
                    f"error_type={run_status.get('error_type')}"
                )
                break
            except Exception as exc:  # noqa: BLE001
                detailed_message = str(exc)
                run_status["error"] = detailed_message
                run_status["status"] = "FAIL"
                run_status["exit_code"] = 1
                run_status["error_type"] = classify_error(exc)
                run_status["error_message"] = flatten_message(detailed_message)
                report.changes.append(f"Pipeline failed without retry: {exc}")
                break
    except Exception as exc:  # noqa: BLE001
        run_status["error"] = str(exc)
        run_status["status"] = "FAIL"
        run_status["exit_code"] = 1
        run_status["error_type"] = classify_error(exc)
        run_status["error_message"] = flatten_message(str(exc))
        report.changes.append(f"Pipeline bootstrap failure: {exc}")
    finally:
        if run_status["status"] == "OK":
            run_status["phase"] = "summarize"
        run_status["ended_at_utc"] = utc_now()
        if args.run_pit:
            write_mutation_summary(pit_reports_dir, run_dir / "mutation_summary.json")
        append_mutation_summary_csv(run_status=run_status, run_dir=run_dir)
        write_per_repo_condition_summary(run_status, run_dir)
        _restore_disabled_tests(module_dir, report)
        write_json(run_dir / "run_status.json", run_status)
        write_patch_report(report, report_path)

    print(f"Wrote patch report: {report_path}")
    print(f"Wrote run artifacts: {run_dir}")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())






