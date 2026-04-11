import json
import os
import csv
from pathlib import Path

dataset_dir = Path("classes2test/classes2test-main/dataset")
output_csv = Path("data/manifests/candidate_repos.csv")

seen_repos = {}

print("Scanning dataset...")
for project_folder in dataset_dir.iterdir():
    if not project_folder.is_dir():
        continue
    for json_file in project_folder.glob("*.json"):
        try:
            with open(json_file, encoding="utf-8") as f:
                data = json.load(f)
            repo = data.get("repository", {})
            repo_id = repo.get("repo_id")
            if repo_id in seen_repos:
                continue
            focal_file = data.get("focal_class", {}).get("file", "")
            test_file = data.get("test_class", {}).get("file", "")
            seen_repos[repo_id] = {
                "repo_id": repo_id,
                "url": repo.get("url", ""),
                "size": repo.get("size", 0),
                "stars": repo.get("stargazer_count", 0),
                "forks": repo.get("fork_count", 0),
                "is_fork": repo.get("is_fork", False),
                "license": repo.get("license", ""),
                "focal_file": focal_file,
                "test_file": test_file,
                "likely_maven": "pom.xml" in focal_file.lower() or "/src/main/java" in focal_file,
            }
            break  # one entry per repo is enough
        except Exception as e:
            print(f"Error reading {json_file}: {e}")

print(f"Found {len(seen_repos)} unique repos")

# Sort by: maven likely first, then by size (smaller = faster to build)
candidates = sorted(
    seen_repos.values(),
    key=lambda r: (not r["likely_maven"], r["is_fork"], r["size"])
)

output_csv.parent.mkdir(parents=True, exist_ok=True)
with open(output_csv, "w", newline="", encoding="utf-8") as f:
    writer = csv.DictWriter(f, fieldnames=candidates[0].keys())
    writer.writeheader()
    writer.writerows(candidates)

print(f"Manifest saved to {output_csv}")
print(f"Top 10 candidates:")
for r in candidates[:10]:
    print(f"  {r['repo_id']} | {r['url']} | size={r['size']} | maven={r['likely_maven']}")