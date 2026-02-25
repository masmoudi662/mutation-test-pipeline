import json
import subprocess
import sys
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parent.parent
DATASET_DIR = BASE_DIR / "classes2test" / "assertion_dataset"
PIPELINE_SCRIPT = BASE_DIR / "experiments" / "run_mutation_pipeline.py"


def extract_repo_id(json_path: Path) -> str:
    return json_path.stem.split("_", 1)[0]


def resolve_module(repo_id: str, mapping: dict) -> str:
    module = mapping.get("module")
    if isinstance(module, str) and module.strip():
        return module.strip()

    project = mapping.get("project")
    if isinstance(project, str) and project.strip():
        return project.strip()

    for key in ("test_class", "focal_class"):
        section = mapping.get(key)
        if isinstance(section, dict):
            file_path = section.get("file")
            if isinstance(file_path, str) and file_path.strip():
                normalized = file_path.replace("\\", "/")
                marker = "/src/"
                if marker in normalized:
                    return normalized.split(marker, 1)[0]

    return repo_id


def run_one_condition(repo_id: str, module: str, condition: str) -> int:
    cmd = [
        sys.executable,
        str(PIPELINE_SCRIPT),
        "--repo-id",
        repo_id,
        "--module",
        module,
        "--condition",
        condition,
        "--test-scope",
        "focal",
        "--run-pit",
        "--build-tool",
        "maven",
    ]
    print(f"[run] {' '.join(cmd)}")
    completed = subprocess.run(cmd, cwd=str(BASE_DIR))
    return completed.returncode


def main() -> int:
    json_files = sorted(DATASET_DIR.glob("*.json"))
    total = len(json_files)
    if total == 0:
        print(f"No dataset JSON files found in {DATASET_DIR}")
        return 1

    failures = 0
    for idx, json_path in enumerate(json_files, start=1):
        repo_id = extract_repo_id(json_path)
        mapping = json.loads(json_path.read_text(encoding="utf-8"))
        module = resolve_module(repo_id, mapping)

        print(f"[{idx}/{total}] Running repo {repo_id} (module={module})")
        for condition in ("HUMAN", "AI_ONLY"):
            rc = run_one_condition(repo_id, module, condition)
            if rc != 0:
                failures += 1
                print(f"[warn] repo={repo_id} module={module} condition={condition} failed with exit code {rc}")

    if failures:
        print(f"Completed with {failures} failed run(s).")
        return 1

    print("Completed successfully with 0 failed runs.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
