"""
run_full_sample_v2.py
Scale-up pipeline: runs 1 JSON per project across N projects from the full
classes2test-main dataset. Clones repos as needed, generates AI tests, then
runs HUMAN and AI_ONLY PIT conditions.

Usage:
    python pipeline/run_full_sample_v2.py [--limit 200] [--offset 0]
"""

import argparse
import csv
import json
import re
import shutil
import subprocess
import sys
import time
from pathlib import Path

# ── Paths ────────────────────────────────────────────────────────────────────
BASE_DIR            = Path(__file__).resolve().parent.parent
DATASET_DIR         = BASE_DIR / "classes2test" / "classes2test-main" / "dataset"
PIPELINE_SCRIPT     = BASE_DIR / "pipeline" / "run_mutation_pipeline.py"
GENERATE_SCRIPT     = BASE_DIR / "pipeline" / "generate_tests.py"
GENERATED_TESTS_DIR = BASE_DIR / "experiments" / "generated_tests"
TARGET_REPOS_DIR    = BASE_DIR / "target-repos"
ARTIFACTS_DIR       = BASE_DIR / "artifacts"
FINAL_COMPARISON_CSV    = ARTIFACTS_DIR / "final_comparison_latest.csv"
REPRODUCIBLE_SUBSET_CSV = ARTIFACTS_DIR / "reproducible_subset.csv"
SUMMARY_CSV             = ARTIFACTS_DIR / "mutation_summary_all_runs.csv"
BUILD_PATCHES_DIR       = ARTIFACTS_DIR / "build_patches"

TARGET_REPOS_DIR.mkdir(exist_ok=True)
GENERATED_TESTS_DIR.mkdir(parents=True, exist_ok=True)
ARTIFACTS_DIR.mkdir(exist_ok=True)

# ── Helpers ───────────────────────────────────────────────────────────────────

def java_file_to_fqcn(path_text: str) -> str:
    normalized = path_text.replace("\\", "/")
    for marker in ("src/test/java/", "src/main/java/"):
        if marker in normalized:
            rel = normalized.split(marker, 1)[1]
            if rel.endswith(".java"):
                rel = rel[:-5]
            return rel.replace("/", ".")
    if normalized.endswith(".java"):
        return normalized[:-5].replace("/", ".")
    raise ValueError(f"Could not derive FQCN from path: {path_text}")


def resolve_module(repo_id: str, mapping: dict) -> str:
    for key in ("module", "project"):
        value = mapping.get(key)
        if isinstance(value, str) and value.strip():
            return value.strip()
    for key in ("test_class", "focal_class"):
        section = mapping.get(key)
        if isinstance(section, dict):
            file_path = section.get("file")
            if isinstance(file_path, str) and file_path.strip():
                normalized = file_path.replace("\\", "/")
                if "/src/" in normalized:
                    return normalized.split("/src/", 1)[0]
    return repo_id


def resolve_commit_hash(mapping: dict) -> str:
    for key in ("commit_hash", "commit", "sha"):
        value = mapping.get(key)
        if isinstance(value, str) and value.strip():
            return value.strip()
    repository = mapping.get("repository")
    if isinstance(repository, dict):
        for key in ("commit_hash", "commit", "sha"):
            value = repository.get(key)
            if isinstance(value, str) and value.strip():
                return value.strip()
    return "HEAD"


def clone_repo(repo_url: str, repo_id: str, commit_hash: str) -> tuple[bool, str]:
    """Clone repo if not already cloned. Returns (success, message)."""
    repo_dir = TARGET_REPOS_DIR / repo_id
    if repo_dir.exists():
        return True, f"already cloned: {repo_dir}"
    try:
        print(f"[clone] Cloning {repo_url} → target-repos/{repo_id}")
        subprocess.run(
            ["git", "clone", "--depth=1", repo_url, str(repo_dir)],
            check=True,
            capture_output=True,
            text=True,
            timeout=120,
        )
        # Checkout specific commit if not HEAD
        if commit_hash and commit_hash != "HEAD":
            try:
                subprocess.run(
                    ["git", "fetch", "--depth=1", "origin", commit_hash],
                    cwd=str(repo_dir),
                    check=False,
                    capture_output=True,
                    text=True,
                    timeout=60,
                )
                subprocess.run(
                    ["git", "checkout", commit_hash],
                    cwd=str(repo_dir),
                    check=False,
                    capture_output=True,
                    text=True,
                    timeout=30,
                )
            except Exception:
                pass  # shallow clone may not have full history; proceed with HEAD
        return True, f"cloned to {repo_dir}"
    except subprocess.TimeoutExpired:
        if repo_dir.exists():
            shutil.rmtree(repo_dir, ignore_errors=True)
        return False, "clone timed out after 120s"
    except subprocess.CalledProcessError as exc:
        if repo_dir.exists():
            shutil.rmtree(repo_dir, ignore_errors=True)
        return False, f"clone failed: {exc.stderr.strip()[:200]}"


def generate_ai_test(json_path: Path) -> tuple[bool, str]:
    """Run generate_tests.py for a single mapping JSON."""
    stem = json_path.stem
    out_file = GENERATED_TESTS_DIR / f"{stem}_GeneratedTest.java"
    if out_file.exists():
        return True, f"already generated: {out_file}"
    try:
        print(f"[generate] Generating AI test for {stem}")
        proc = subprocess.run(
            [sys.executable, str(GENERATE_SCRIPT), "--json-path", str(json_path)],
            cwd=str(BASE_DIR),
            capture_output=True,
            text=True,
            timeout=120,
        )
        if out_file.exists():
            return True, f"generated: {out_file}"
        return False, f"generate script exited {proc.returncode}: {proc.stderr.strip()[:200]}"
    except subprocess.TimeoutExpired:
        return False, "generation timed out after 120s"
    except Exception as exc:
        return False, f"generation failed: {exc}"


def normalize_error_type(error_type: str, error_message: str = "") -> str:
    CATEGORIES = [
        "COMPILATION", "DEP_RESOLUTION", "HTTP_BLOCKER", "SNAPSHOT_MISSING",
        "MODULE_MISSING", "FILE_NOT_FOUND", "PIT_ERROR", "TEST_FAILURE",
        "TIMEOUT", "CLONE_FAILED", "GENERATE_FAILED", "OTHER",
    ]
    value = (error_type or "").strip().upper()
    hay = f"{value}\n{error_message}".lower()
    if value in CATEGORIES:
        return value
    if "timed out" in hay or "timeoutexpired" in hay:
        return "TIMEOUT"
    if "pitest" in hay or "mutationcoveragereport" in hay:
        return "PIT_ERROR"
    if "http_blocker" in hay or "blocked mirror" in hay:
        return "HTTP_BLOCKER"
    if "snapshot" in hay and "missing" in hay:
        return "SNAPSHOT_MISSING"
    if "dependency" in hay and "resolve" in hay:
        return "DEP_RESOLUTION"
    if "compilation" in hay:
        return "COMPILATION"
    if "test failure" in hay:
        return "TEST_FAILURE"
    if "module directory does not exist" in hay:
        return "MODULE_MISSING"
    return "OTHER"


def _flatten(text: str, max_len: int = 400) -> str:
    return " ".join((text or "").split())[:max_len]


def find_latest_run_status(repo_id: str, module: str, condition: str, start_time: float) -> dict | None:
    runs_dir = ARTIFACTS_DIR / "runs"
    if not runs_dir.exists():
        return None
    candidates = []
    for status_path in runs_dir.glob("*/run_status.json"):
        try:
            if status_path.stat().st_mtime < (start_time - 5):
                continue
            payload = json.loads(status_path.read_text(encoding="utf-8"))
        except Exception:
            continue
        if payload.get("repo_id") != repo_id:
            continue
        if payload.get("module") != module:
            continue
        if payload.get("condition") != condition:
            continue
        payload["run_dir"] = str(status_path.parent)
        candidates.append(payload)
    if not candidates:
        return None
    return max(candidates, key=lambda x: x.get("ended_at_utc") or x.get("started_at_utc") or "")


def ensure_repo_settings_https(repo_id: str) -> Path:
    repo_root = TARGET_REPOS_DIR / repo_id
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
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>allow-https-repos</activeProfile>
  </activeProfiles>
</settings>""",
        encoding="utf-8",
    )
    return settings_path


def clear_http_blocker_cache(error_message: str) -> int:
    m2_repo = Path.home() / ".m2" / "repository"
    if not m2_repo.exists():
        return 0
    groups: set[str] = set()
    for group_id, _ in re.findall(r"([A-Za-z0-9_.-]+):([A-Za-z0-9_.-]+):", error_message or ""):
        if "." in group_id:
            groups.add(group_id)
    removed = 0
    for group_id in sorted(groups):
        target = m2_repo / Path(*group_id.split("."))
        if not target.exists():
            continue
        try:
            shutil.rmtree(target)
            removed += 1
        except OSError:
            pass
    return removed


def cleanup_repo_state(repo_id: str) -> None:
    repo_root = TARGET_REPOS_DIR / repo_id
    if not repo_root.exists():
        return
    for cmd in (["git", "reset", "--hard"], ["git", "clean", "-fdx"]):
        subprocess.run(cmd, cwd=str(repo_root), capture_output=True, check=False)


def run_one_condition(
    repo_id: str,
    module: str,
    condition: str,
    mapping_json: Path,
    retries: int = 0,
    fixed_by: str = "NONE",
    mvn_force_update: bool = False,
    settings_override: Path | None = None,
) -> dict:
    cmd = [
        sys.executable,
        str(PIPELINE_SCRIPT),
        "--repo-id", repo_id,
        "--module", module,
        "--condition", condition,
        "--test-scope", "focal",
        "--run-pit",
        "--build-tool", "maven",
        "--mapping-json", str(mapping_json),
        "--retries", str(retries),
        "--fixed-by", fixed_by,
    ]
    if mvn_force_update:
        cmd.append("--mvn-force-update")
    if settings_override is not None:
        cmd.extend(["--settings-override", str(settings_override)])

    print(f"[run] {' '.join(cmd)}")
    start_time = time.time()
    try:
        completed = subprocess.run(cmd, cwd=str(BASE_DIR), check=False)
        returncode = completed.returncode
    except Exception as exc:
        return {
            "returncode": 1, "status": "FAIL", "phase": "invoke",
            "error_type": "OTHER", "error_message": _flatten(str(exc)),
            "run_dir": "", "retries": retries, "fixed_by": fixed_by,
            "timestamp": "", "mutation_score_pct": "",
            "mutations_generated": "", "mutations_killed": "", "mutations_survived": "",
        }

    status = find_latest_run_status(repo_id, module, condition, start_time)
    if not status:
        status = {
            "status": "OK" if returncode == 0 else "FAIL",
            "phase": "", "error_type": "OTHER" if returncode != 0 else "",
            "error_message": "", "run_dir": "", "retries": retries,
            "fixed_by": fixed_by, "ended_at_utc": "",
        }

    mutation_score_pct = mutations_generated = mutations_killed = mutations_survived = ""
    run_dir = status.get("run_dir", "")
    if run_dir:
        summary_path = Path(run_dir) / "mutation_summary.json"
        if summary_path.exists():
            try:
                s = json.loads(summary_path.read_text(encoding="utf-8"))
                total   = int(s.get("total_mutations", 0) or 0)
                killed  = int(s.get("killed_mutations", 0) or 0)
                survived = int(s.get("survived_mutations", 0) or 0)
                score   = float(s.get("mutation_score", 0.0) or 0.0)
                mutation_score_pct  = f"{(score * 100.0):.6f}"
                mutations_generated = str(total)
                mutations_killed    = str(killed)
                mutations_survived  = str(survived)
            except Exception:
                pass

    result = {
        "returncode": returncode,
        "status": status.get("status", "FAIL"),
        "phase": status.get("phase", ""),
        "error_type": normalize_error_type(status.get("error_type", ""), status.get("error_message", ""))
                      if status.get("status") != "OK" else "",
        "error_message": _flatten(status.get("error_message", "")),
        "run_dir": run_dir,
        "retries": int(status.get("retries", retries) or retries),
        "fixed_by": status.get("fixed_by", fixed_by),
        "timestamp": status.get("ended_at_utc") or status.get("started_at_utc") or "",
        "mutation_score_pct": mutation_score_pct,
        "mutations_generated": mutations_generated,
        "mutations_killed": mutations_killed,
        "mutations_survived": mutations_survived,
    }
    print(
        f"[result] repo={repo_id} module={module} condition={condition} "
        f"status={result['status']} error_type={result['error_type'] or 'NONE'} "
        f"mutation_score={result['mutation_score_pct'] or 'NA'}"
    )
    return result


def determine_fix_strategy(error_type: str, error_message: str) -> str:
    hay = f"{error_type}\n{error_message}".lower()
    if "http_blocker" in hay or "blocked mirror" in hay or "maven-default-http-blocker" in hay:
        return "HTTP_SETTINGS_FIX"
    if "snapshot_missing" in hay or "was not found in" in hay or "failure to find" in hay:
        return "MAVEN_FORCE_UPDATE"
    return "NONE"


def write_paired_outputs(per_repo_records: list[dict]) -> int:
    FINAL_COMPARISON_CSV.parent.mkdir(parents=True, exist_ok=True)
    header = [
        "repo_id", "module", "focal_class", "commit_hash",
        "human_ok", "human_score_pct", "human_total", "human_killed", "human_survived", "human_error",
        "ai_ok", "ai_score_pct", "ai_total", "ai_killed", "ai_survived", "ai_error",
        "delta_score_pct", "run_timestamp",
    ]
    valid_pairs = 0
    with FINAL_COMPARISON_CSV.open("w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=header)
        writer.writeheader()
        for rec in per_repo_records:
            human = rec.get("HUMAN", {})
            ai    = rec.get("AI_ONLY", {})
            human_ok = human.get("status") == "OK"
            ai_ok    = ai.get("status") == "OK"
            if human_ok and ai_ok:
                valid_pairs += 1
            human_score = float(human.get("mutation_score_pct") or "0")
            ai_score    = float(ai.get("mutation_score_pct") or "0")
            delta = f"{(ai_score - human_score):.6f}" if human_ok and ai_ok else ""
            writer.writerow({
                "repo_id":         rec.get("repo_id", ""),
                "module":          rec.get("module", ""),
                "focal_class":     rec.get("focal_class", ""),
                "commit_hash":     rec.get("commit_hash", ""),
                "human_ok":        "TRUE" if human_ok else "FALSE",
                "human_score_pct": human.get("mutation_score_pct", ""),
                "human_total":     human.get("mutations_generated", ""),
                "human_killed":    human.get("mutations_killed", ""),
                "human_survived":  human.get("mutations_survived", ""),
                "human_error":     human.get("error_type", ""),
                "ai_ok":           "TRUE" if ai_ok else "FALSE",
                "ai_score_pct":    ai.get("mutation_score_pct", ""),
                "ai_total":        ai.get("mutations_generated", ""),
                "ai_killed":       ai.get("mutations_killed", ""),
                "ai_survived":     ai.get("mutations_survived", ""),
                "ai_error":        ai.get("error_type", ""),
                "delta_score_pct": delta,
                "run_timestamp":   time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
            })

    with REPRODUCIBLE_SUBSET_CSV.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["repo_id", "module"])
        for rec in per_repo_records:
            if rec.get("HUMAN", {}).get("status") == "OK" and rec.get("AI_ONLY", {}).get("status") == "OK":
                writer.writerow([rec.get("repo_id", ""), rec.get("module", "")])

    return valid_pairs


def pick_one_json_per_project() -> list[Path]:
    """Pick one JSON file per project folder, sorted by project ID."""
    result = []
    for project_folder in sorted(DATASET_DIR.iterdir()):
        if not project_folder.is_dir():
            continue
        jsons = sorted(project_folder.glob("*.json"))
        if jsons:
            result.append(jsons[0])
    return result


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Scale-up pipeline: 1 JSON per project.")
    parser.add_argument("--limit",  type=int, default=200, help="Max projects to process (default 200)")
    parser.add_argument("--offset", type=int, default=0,   help="Skip first N projects (default 0)")
    return parser.parse_args()


def main() -> int:
    args = parse_args()

    print(f"[config] Dataset: {DATASET_DIR}")
    print(f"[config] Limit: {args.limit} | Offset: {args.offset}")

    all_jsons = pick_one_json_per_project()
    batch = all_jsons[args.offset : args.offset + args.limit]
    total = len(batch)
    print(f"[config] Selected {total} projects to process")

    per_repo_records: list[dict] = []
    all_results: list[dict] = []

    for idx, json_path in enumerate(batch, start=1):
        mapping = json.loads(json_path.read_text(encoding="utf-8"))
        repo_info  = mapping.get("repository", {})
        repo_id    = str(repo_info.get("repo_id", json_path.parent.name))
        repo_url   = repo_info.get("url", "")
        commit_hash = resolve_commit_hash(mapping)
        module     = resolve_module(repo_id, mapping)
        focal_class = ""
        try:
            focal_class = java_file_to_fqcn(mapping["focal_class"]["file"])
        except Exception:
            pass

        print(f"\n[{idx}/{total}] repo={repo_id} module={module} url={repo_url}")

        record = {
            "repo_id": repo_id,
            "module": module,
            "focal_class": focal_class,
            "commit_hash": commit_hash,
            "HUMAN": {},
            "AI_ONLY": {},
            "applied_fixes": [],
        }

        # ── Step 1: Clone ─────────────────────────────────────────────────
        if repo_url:
            clone_ok, clone_msg = clone_repo(repo_url, repo_id, commit_hash)
            print(f"[clone] {clone_msg}")
            if not clone_ok:
                for condition in ("HUMAN", "AI_ONLY"):
                    entry = {
                        "status": "FAIL", "error_type": "CLONE_FAILED",
                        "error_message": clone_msg, "mutation_score_pct": "",
                        "mutations_generated": "", "mutations_killed": "", "mutations_survived": "",
                    }
                    record[condition] = entry
                    all_results.append({"repo_id": repo_id, "module": module, "condition": condition, **entry})
                per_repo_records.append(record)
                continue
        else:
            print(f"[warn] No repo URL for {repo_id}, skipping")
            per_repo_records.append(record)
            continue

        # ── Step 2: Generate AI test ──────────────────────────────────────
        gen_ok, gen_msg = generate_ai_test(json_path)
        print(f"[generate] {gen_msg}")
        if not gen_ok:
            print(f"[warn] AI test generation failed for {repo_id}, will attempt AI_ONLY anyway")

        # ── Step 3: Run HUMAN and AI_ONLY conditions ──────────────────────
        for condition in ("HUMAN", "AI_ONLY"):
            first = run_one_condition(repo_id, module, condition, json_path)
            final_entry = first
            cleanup_repo_state(repo_id)

            # Retry logic
            if first.get("status") != "OK":
                strategy = determine_fix_strategy(first.get("error_type", ""), first.get("error_message", ""))
                if strategy == "HTTP_SETTINGS_FIX":
                    removed = clear_http_blocker_cache(first.get("error_message", ""))
                    if removed:
                        print(f"[fix] cleared {removed} HTTP-blocked cache paths")
                    settings_path = ensure_repo_settings_https(repo_id)
                    retry = run_one_condition(
                        repo_id, module, condition, json_path,
                        retries=1, fixed_by="HTTP_SETTINGS_FIX",
                        mvn_force_update=True, settings_override=settings_path,
                    )
                    final_entry = retry
                    record["applied_fixes"].append("HTTP_SETTINGS_FIX")
                    cleanup_repo_state(repo_id)
                elif strategy == "MAVEN_FORCE_UPDATE":
                    retry = run_one_condition(
                        repo_id, module, condition, json_path,
                        retries=1, fixed_by="MAVEN_FORCE_UPDATE", mvn_force_update=True,
                    )
                    final_entry = retry
                    record["applied_fixes"].append("MAVEN_FORCE_UPDATE")
                    cleanup_repo_state(repo_id)

            record[condition] = final_entry
            all_results.append({"repo_id": repo_id, "module": module, "condition": condition, **final_entry})

        record["applied_fixes"] = sorted(dict.fromkeys(record["applied_fixes"]))
        per_repo_records.append(record)

        # ── Progress summary ──────────────────────────────────────────────
        human_ok = sum(1 for r in per_repo_records if r.get("HUMAN", {}).get("status") == "OK")
        ai_ok    = sum(1 for r in per_repo_records if r.get("AI_ONLY", {}).get("status") == "OK")
        pairs    = sum(
            1 for r in per_repo_records
            if r.get("HUMAN", {}).get("status") == "OK" and r.get("AI_ONLY", {}).get("status") == "OK"
        )
        print(f"[progress] {idx}/{total} | HUMAN_OK={human_ok} AI_OK={ai_ok} PAIRS={pairs}")

        # Save intermediate results every 10 repos
        if idx % 10 == 0:
            write_paired_outputs(per_repo_records)
            print(f"[saved] Intermediate results written ({pairs} valid pairs so far)")

    # ── Final output ──────────────────────────────────────────────────────────
    valid_pairs = write_paired_outputs(per_repo_records)

    human_ok_count = sum(1 for r in per_repo_records if r.get("HUMAN", {}).get("status") == "OK")
    ai_ok_count    = sum(1 for r in per_repo_records if r.get("AI_ONLY", {}).get("status") == "OK")

    print("\n" + "="*60)
    print(f"TOTAL_REPOS_ATTEMPTED : {total}")
    print(f"HUMAN_OK_COUNT        : {human_ok_count}")
    print(f"AI_ONLY_OK_COUNT      : {ai_ok_count}")
    print(f"VALID_PAIRED_COUNT    : {valid_pairs}")
    print(f"FINAL_COMPARISON_PATH : {FINAL_COMPARISON_CSV}")
    print(f"REPRODUCIBLE_SUBSET   : {REPRODUCIBLE_SUBSET_CSV}")
    print("="*60)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
