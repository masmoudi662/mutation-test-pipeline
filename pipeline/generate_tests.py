import json
import traceback
import subprocess
from pathlib import Path
import os
from datetime import datetime, timezone

import vertexai
from vertexai.generative_models import GenerativeModel

# ---------------- CONFIG ----------------
PROJECT_ID = "true-winter-487215-g3"
LOCATION = "us-central1"
MODEL_NAME = "gemini-2.0-flash"

BASE_DIR = Path(__file__).resolve().parent.parent

TARGETS_DIR = BASE_DIR / "classes2test" / "classes2test-main" / "dataset"

# keep one clone per repo (so we don't overwrite target-repo)
TARGET_REPOS_DIR = BASE_DIR / "target-repos"
TARGET_REPOS_DIR.mkdir(exist_ok=True)

OUT_DIR = BASE_DIR / "experiments" / "generated_tests"
OUT_DIR.mkdir(exist_ok=True)

FAILURES_DIR = BASE_DIR / "logs" / "generation_failures"
FAILURES_DIR.mkdir(parents=True, exist_ok=True)

os.environ.pop("GOOGLE_APPLICATION_CREDENTIALS", None)

vertexai.init(project=PROJECT_ID, location=LOCATION)
model = GenerativeModel(MODEL_NAME)

# ---------------- GIT HELPERS ----------------


def run_git(repo_dir: Path, args: list[str]) -> str:
    """
    Run git in repo_dir and return stdout (stripped).
    Raises CalledProcessError if git fails.
    """
    out = subprocess.check_output(
        ["git", *args],
        cwd=str(repo_dir),
        stderr=subprocess.STDOUT,
    )
    return out.decode("utf-8", errors="replace").strip()


def checkout_detached(repo_dir: Path, commit: str) -> None:
    """Checkout repo to a specific commit (detached HEAD)."""
    run_git(repo_dir, ["checkout", "--quiet", commit])


def try_find_commit_with_path(repo_dir: Path, rel_path: str) -> str | None:
    """
    Return a commit SHA that contains rel_path at some point in history, else None.
    We use: git rev-list --all -- <path>  (commits that touched that path)
    """
    try:
        out = run_git(repo_dir, ["rev-list", "--all", "--", rel_path])
        if not out:
            return None
        return out.splitlines()[0].strip()
    except Exception:
        return None


def with_alt_extensions(p: str) -> list[str]:
    """
    If p ends with .java, also try .kt/.kts.
    If p ends with .kt/.kts, also try .java.
    """
    alts = [p]
    if p.endswith(".java"):
        alts.append(p[:-5] + ".kt")
        alts.append(p[:-5] + ".kts")
    elif p.endswith(".kt"):
        alts.append(p[:-3] + ".java")
        alts.append(p[:-3] + ".kts")
    elif p.endswith(".kts"):
        alts.append(p[:-4] + ".java")
        alts.append(p[:-4] + ".kt")
    return alts


# ---------------- HELPERS ----------------


def load_target(json_path: Path) -> dict:
    with json_path.open("r", encoding="utf-8") as f:
        return json.load(f)


def _minimal_class_context(focal_source: str, max_lines: int = 80) -> str:
    lines = focal_source.splitlines()
    package_line = ""
    import_lines = []
    class_line = ""
    for line in lines:
        stripped = line.strip()
        if not package_line and stripped.startswith("package "):
            package_line = line
        elif stripped.startswith("import ") and len(import_lines) < 12:
            import_lines.append(line)
        if not class_line and (" class " in f" {stripped} " or stripped.startswith("class ")):
            class_line = line

    context_lines = []
    if package_line:
        context_lines.append(package_line)
    context_lines.extend(import_lines)
    if class_line:
        context_lines.append(class_line)

    if not context_lines:
        context_lines = lines[:max_lines]

    return "\n".join(context_lines[:max_lines])


def prompt_for_target(target: dict, focal_source: str) -> str:
    focal_path = target["focal_class"]["file"]
    repo_url = target["repository"]["url"]
    focal_method = target.get("focal_method", {}).get("body", "")
    minimal_context = _minimal_class_context(focal_source)

    return f"""
You are generating Java unit tests.

Repository:
{repo_url}

Focal file path:
{focal_path}

Rules:
- Output ONLY one Java file (complete source code).
- Must compile with Maven.
- Use the test framework already used in the repository.
- Correct package declaration required.
- Max 10 test methods.
- No explanations.
- No markdown.
- Only valid Java code.

Minimal focal class context:
{minimal_context}

Focal method:
{focal_method}
""".strip()


def ensure_repo_cloned(repo_url: str, repo_id: str, checkout_ref: str | None = None) -> Path:
    """
    Clone repo into target-repos/<repo_id>/ if not already present.
    Returns the path to the cloned repo folder.
    """
    repo_dir = TARGET_REPOS_DIR / repo_id
    if not (repo_dir.exists() and any(repo_dir.iterdir())):
        print("CLONING REPO INTO:", repo_dir)
        subprocess.run(["git", "clone", repo_url, str(repo_dir)], check=True)

    # If dataset provides a ref/commit/revision, honor it.
    if checkout_ref:
        subprocess.run(["git", "-C", str(repo_dir), "checkout", checkout_ref], check=True)

    return repo_dir


def resolve_focal_path(repo_dir: Path, focal_rel_path: str) -> Path:
    """
    Resolve focal_rel_path inside repo_dir.

    Strategy:
      1) direct path
      2) alt extension (.java <-> .kt/.kts)
      3) suffix match inside repo
      4) filename fallback search
      5) if still missing: try checkout a commit in history that contains the path, then retry 1-4
    """
    repo_dir = Path(repo_dir)

    def _attempt_resolve(rel_path: str) -> Path | None:
        # 1) direct
        direct = repo_dir / rel_path
        if direct.exists():
            return direct

        # 2) alt extensions
        for alt in with_alt_extensions(rel_path):
            altp = repo_dir / alt
            if altp.exists():
                return altp

        # 3) suffix match (against full relative path)
        normalized = rel_path.replace("\\", "/")
        filename = Path(normalized).name

        suffix_hits = []
        for candidate in repo_dir.rglob(filename):
            if not candidate.is_file():
                continue
            candidate_norm = candidate.as_posix()
            if candidate_norm.endswith(normalized):
                suffix_hits.append(candidate)

        if suffix_hits:
            return sorted(suffix_hits, key=lambda p: p.as_posix())[0]

        # 4) filename fallback
        all_hits = sorted(
            [p for p in repo_dir.rglob(filename) if p.is_file()],
            key=lambda p: p.as_posix(),
        )
        if all_hits:
            return all_hits[0]

        return None

    # First attempt on current checkout
    found = _attempt_resolve(focal_rel_path)
    if found is not None:
        return found

    # 5) Try history: find a commit where the path exists (or alt extension exists), checkout, retry
    for candidate_path in with_alt_extensions(focal_rel_path):
        commit = try_find_commit_with_path(repo_dir, candidate_path)
        if commit:
            try:
                checkout_detached(repo_dir, commit)
            except Exception:
                continue
            found2 = _attempt_resolve(candidate_path)
            if found2 is not None:
                return found2

    raise FileNotFoundError(f"Unable to resolve focal path {focal_rel_path} inside {repo_dir}")


def call_vertex_with_retry(prompt: str, attempts: int = 3) -> str:
    last_error = None
    for attempt in range(1, attempts + 1):
        print(f"CALLING VERTEX AI... attempt {attempt}/{attempts}")
        try:
            response = model.generate_content(prompt)
            generated_code = (response.text or "").strip()

            # strip fenced code if model returns it
            if generated_code.startswith("```"):
                parts = generated_code.split("```")
                if len(parts) >= 2:
                    generated_code = parts[1].strip().replace("```", "").strip()

            if generated_code:
                return generated_code

            last_error = ValueError("Vertex returned empty response.")
        except Exception as exc:  # noqa: BLE001
            last_error = exc
    raise RuntimeError(f"Vertex call failed after {attempts} attempts: {last_error}")


def write_failure_report(json_path: Path, error: Exception) -> Path:
    report = {
        "json_file": str(json_path),
        "basename": json_path.stem,
        "error_type": type(error).__name__,
        "error_message": str(error),
        "traceback": traceback.format_exc(),
        "timestamp_utc": datetime.now(timezone.utc).isoformat(),
    }
    out_path = FAILURES_DIR / f"{json_path.stem}.json"
    out_path.write_text(json.dumps(report, indent=2), encoding="utf-8")
    return out_path


# ---------------- MAIN ----------------


def main():
    print("TARGETS_DIR =", TARGETS_DIR)

    json_files = sorted(TARGETS_DIR.glob("*.json"))
    print("JSON FILES FOUND =", len(json_files))

    if not json_files:
        raise SystemExit("No JSON files found.")

    succeeded = 0
    failed = 0
    failed_basenames = []

    for json_path in json_files:
        print("\nUSING JSON =", json_path.name)
        try:
            target = load_target(json_path)

            repo_id = str(target["repository"].get("repo_id", json_path.stem))
            repo_url = target["repository"]["url"]
            checkout_ref = (
                target.get("repository", {}).get("commit")
                or target.get("repository", {}).get("revision")
                or target.get("repository", {}).get("ref")
            )

            print("REPO_ID =", repo_id)
            print("REPO_URL =", repo_url)
            if checkout_ref:
                print("CHECKOUT_REF =", checkout_ref)

            repo_dir = ensure_repo_cloned(repo_url, repo_id, checkout_ref=checkout_ref)

            focal_rel_path = target["focal_class"]["file"]
            print("FOCAL_REL_PATH =", focal_rel_path)

            focal_abs_path = resolve_focal_path(repo_dir, focal_rel_path)
            print("FOCAL_ABS_PATH =", focal_abs_path)

            focal_source = focal_abs_path.read_text(encoding="utf-8", errors="replace")

            prompt = prompt_for_target(target, focal_source)

            generated_code = call_vertex_with_retry(prompt, attempts=3)

            out_path = OUT_DIR / f"{json_path.stem}_GeneratedTest.java"
            out_path.write_text(generated_code, encoding="utf-8")

            print("WROTE:", out_path.resolve())
            succeeded += 1

        except Exception as exc:
            failed += 1
            failed_basenames.append(json_path.stem)
            print(f"ERROR processing {json_path.name}: {exc}")
            failure_report = write_failure_report(json_path, exc)
            print("WROTE FAILURE REPORT:", failure_report)
            continue

    print("\n=== GENERATION SUMMARY ===")
    print("TOTAL JSON FILES =", len(json_files))
    print("SUCCEEDED =", succeeded)
    print("FAILED =", failed)
    if failed_basenames:
        print("FAILED FILES =", ", ".join(failed_basenames))


if __name__ == "__main__":
    main()