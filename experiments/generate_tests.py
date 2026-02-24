import json
import subprocess
from pathlib import Path
import os

import vertexai
from vertexai.generative_models import GenerativeModel

# ---------------- CONFIG ----------------
PROJECT_ID = "true-winter-487215-g3"
LOCATION = "us-central1"
MODEL_NAME = "gemini-2.0-flash"

BASE_DIR = Path(__file__).resolve().parent.parent

TARGETS_DIR = BASE_DIR / "classes2test" / "assertion_dataset"

# NEW: keep one clone per repo (so we don't overwrite target-repo)
TARGET_REPOS_DIR = BASE_DIR / "target-repos"
TARGET_REPOS_DIR.mkdir(exist_ok=True)

OUT_DIR = Path(__file__).resolve().parent / "generated_tests"
OUT_DIR.mkdir(exist_ok=True)
os.environ.pop("GOOGLE_APPLICATION_CREDENTIALS", None)
vertexai.init(project=PROJECT_ID, location=LOCATION)
model = GenerativeModel(MODEL_NAME)


# ---------------- HELPERS ----------------

def load_target(json_path: Path) -> dict:
    with json_path.open("r", encoding="utf-8") as f:
        return json.load(f)


def prompt_for_target(target: dict, focal_source: str) -> str:
    focal_path = target["focal_class"]["file"]
    repo_url = target["repository"]["url"]
    focal_method = target.get("focal_method", {}).get("body", "")

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

Focal class source:
{focal_source}

Focal method (if helpful):
{focal_method}
""".strip()


def ensure_repo_cloned(repo_url: str, repo_id: str) -> Path:
    """
    Clone repo into target-repos/<repo_id>/ if not already present.
    Returns the path to the cloned repo folder.
    """
    repo_dir = TARGET_REPOS_DIR / repo_id
    if repo_dir.exists() and any(repo_dir.iterdir()):
        return repo_dir

    print("CLONING REPO INTO:", repo_dir)
    subprocess.run(["git", "clone", repo_url, str(repo_dir)], check=True)
    return repo_dir


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
            print("REPO_ID =", repo_id)
            print("REPO_URL =", repo_url)

            repo_dir = ensure_repo_cloned(repo_url, repo_id)

            focal_rel_path = target["focal_class"]["file"]
            print("FOCAL_REL_PATH =", focal_rel_path)

            focal_abs_path = repo_dir / focal_rel_path
            print("FOCAL_ABS_PATH =", focal_abs_path)

            if not focal_abs_path.exists():
                raise FileNotFoundError(
                    f"Focal class file not found: {focal_abs_path} (repo_dir={repo_dir})"
                )

            focal_source = focal_abs_path.read_text(encoding="utf-8", errors="replace")

            prompt = prompt_for_target(target, focal_source)

            print("CALLING VERTEX AI...")
            response = model.generate_content(prompt)

            generated_code = (response.text or "").strip()

            # Remove markdown fences if present
            if generated_code.startswith("```"):
                parts = generated_code.split("```")
                if len(parts) >= 2:
                    generated_code = parts[1].strip()
                    generated_code = generated_code.replace("```", "").strip()
            if not generated_code:
                raise ValueError("Vertex returned empty response.")

            out_path = OUT_DIR / f"{json_path.stem}_GeneratedTest.java"
            out_path.write_text(generated_code, encoding="utf-8")

            print("WROTE:", out_path.resolve())
            succeeded += 1
        except Exception as exc:
            failed += 1
            failed_basenames.append(json_path.stem)
            print(f"ERROR processing {json_path.name}: {exc}")
            continue

    print("\n=== GENERATION SUMMARY ===")
    print("TOTAL JSON FILES =", len(json_files))
    print("SUCCEEDED =", succeeded)
    print("FAILED =", failed)
    if failed_basenames:
        print("FAILED FILES =", ", ".join(failed_basenames))


if __name__ == "__main__":
    main()
