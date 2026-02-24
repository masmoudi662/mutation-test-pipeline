import argparse
import json
import shutil
import subprocess
import sys
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
) -> None:
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
        raise subprocess.CalledProcessError(
            proc.returncode, cmd, output=proc.stdout, stderr=proc.stderr
        )


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


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Run Maven test/PIT with deterministic buildability patches."
    )
    parser.add_argument("--repo-id", required=True, help="Target repository id, e.g. 103035")
    parser.add_argument(
        "--module",
        required=True,
        help="Module directory inside target-repos/<repo-id>.",
    )
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
    parser.add_argument(
        "--run-pit",
        action="store_true",
        help="Also run PIT after tests.",
    )
    parser.add_argument(
        "--pit-goal",
        default="org.pitest:pitest-maven:1.6.9:mutationCoverage",
        help="PIT goal to execute.",
    )
    parser.add_argument(
        "--mvn-cmd",
        default="mvn.cmd",
        help="Maven executable name/path.",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    repo_root = BASE_DIR / "target-repos" / args.repo_id
    module_dir = repo_root / args.module
    if not module_dir.exists():
        raise SystemExit(f"Module directory does not exist: {module_dir}")

    run_id = f"{datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%SZ')}_{args.repo_id}_{args.module}_{args.condition}_{args.test_scope}"
    run_dir = BASE_DIR / "artifacts" / "runs" / run_id
    run_dir.mkdir(parents=True, exist_ok=True)

    run_status = {
        "run_id": run_id,
        "repo_id": args.repo_id,
        "module": args.module,
        "condition": args.condition,
        "test_scope": args.test_scope,
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

    report_path = BASE_DIR / "artifacts" / "build_patches" / f"{args.repo_id}_{args.module}.json"

    mapping = None
    selected_tests: list[str] = []
    focal_class = None
    pit_reports_dir = module_dir / "target" / "pit-reports"
    copied_pit_dir = run_dir / "pit-reports"

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
            selected_tests, focal_class = resolve_focal_selection(
                module_dir, args.module, mapping, args.condition
            )
            keep_fqcns = set(selected_tests)
            _disable_unselected_tests(module_dir, keep_fqcns, report)
            report.changes.append(f"Applied focal test scope with selected tests: {selected_tests}")

        run_status["selected_test_classes"] = selected_tests

        install_missing_jars(BASE_DIR, settings_path, report, mvn_cmd=args.mvn_cmd)

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

        if args.run_pit:
            if mapping is None:
                raise RuntimeError("PIT execution requires mapping JSON for focal target classes/tests.")
            if not focal_class:
                focal_class = java_file_to_fqcn(mapping["focal_class"]["file"])
            run_status["target_classes"] = [focal_class]

            shutil.rmtree(pit_reports_dir, ignore_errors=True)

            pit_cmd = [
                args.mvn_cmd,
                "-s",
                str(settings_path),
                args.pit_goal,
                f"-DtargetClasses={focal_class}",
                f"-DtargetTests={','.join(selected_tests)}",
                "-DoutputFormats=XML,HTML",
            ]
            run_command(pit_cmd, module_dir, report, run_status, run_dir, "pit")

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
            if pit_reports_dir.exists():
                shutil.copytree(pit_reports_dir, copied_pit_dir, dirs_exist_ok=True)
            write_mutation_summary(copied_pit_dir, run_dir / "mutation_summary.json")
        _restore_disabled_tests(module_dir, report)
        write_json(run_dir / "run_status.json", run_status)
        write_patch_report(report, report_path)

    print(f"Wrote patch report: {report_path}")
    print(f"Wrote run artifacts: {run_dir}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
