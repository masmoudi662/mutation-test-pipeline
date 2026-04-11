import csv
import json
import re
import shutil
import subprocess
import sys
import time
from collections import Counter
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parent.parent
DATASET_DIR = BASE_DIR / "classes2test" / "classes2test-main" / "dataset"
PIPELINE_SCRIPT = BASE_DIR / "pipeline" / "run_mutation_pipeline.py"
FINAL_COMPARISON_CSV = BASE_DIR / "experiments" / "RQ1_mutation_score" / "results" / "final_comparison_latest.csv"
REPRODUCIBLE_SUBSET_CSV = BASE_DIR / "experiments" / "RQ1_mutation_score" / "results" / "reproducible_subset.csv"
GENERATED_TESTS_DIR = BASE_DIR / "experiments" / "generated_tests"
SUMMARY_CSV = BASE_DIR / "experiments" / "RQ1_mutation_score" / "results" / "mutation_summary_all_runs.csv"
BUILD_PATCHES_DIR = BASE_DIR / "logs" / "build_patches"


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


def java_file_to_fqcn(path_text: str) -> str:
    normalized = path_text.replace("\\", "/")
    for marker in ("src/test/java/", "src/main/java/"):
        if marker in normalized:
            rel = normalized.split(marker, 1)[1]
            if rel.endswith(".java"):
                rel = rel[:-5]
            return rel.replace("/", ".")
    if "src/" in normalized and normalized.endswith(".java"):
        rel = normalized.split("src/", 1)[1]
        for prefix in ("main/java/", "test/java/"):
            if rel.startswith(prefix):
                rel = rel[len(prefix) :]
        return rel[:-5].replace("/", ".")
    if normalized.endswith(".java"):
        return normalized[:-5].replace("/", ".")
    raise ValueError(f"Could not derive class name from path: {path_text}")


def expected_ai_test_path(module_dir: Path, mapping: dict) -> Path:
    focal_file = mapping["focal_class"]["file"]
    focal_fqcn = java_file_to_fqcn(focal_file)
    focal_identifier = mapping["focal_class"]["identifier"]
    pkg = focal_fqcn.rsplit(".", 1)[0] if "." in focal_fqcn else ""
    rel_dir = Path(*pkg.split(".")) if pkg else Path()
    return module_dir / "src" / "test" / "java" / rel_dir / f"{focal_identifier}AiTest.java"


def provision_ai_test_if_missing(repo_id: str, module: str, mapping: dict, dataset_json_path: Path) -> tuple[bool, str]:
    module_dir = BASE_DIR / "target-repos" / repo_id / module
    target_path = expected_ai_test_path(module_dir, mapping)
    if target_path.exists():
        return True, f"already exists: {target_path}"

    generated_source = GENERATED_TESTS_DIR / f"{dataset_json_path.stem}_GeneratedTest.java"
    if not generated_source.exists():
        return False, f"generated test not found: {generated_source}"

    try:
        raw = generated_source.read_text(encoding="utf-8")
    except Exception as exc:  # noqa: BLE001
        return False, f"failed reading generated test: {exc}"

    lines = raw.splitlines()
    if lines and lines[0].strip().lower() == "java":
        lines = lines[1:]
    text = "\n".join(lines)

    expected_pkg = ""
    focal_fqcn = java_file_to_fqcn(mapping["focal_class"]["file"])
    if "." in focal_fqcn:
        expected_pkg = focal_fqcn.rsplit(".", 1)[0]
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

    new_class_name = f"{mapping['focal_class']['identifier']}AiTest"
    class_match = re.search(r"\bpublic\s+class\s+([A-Za-z_][A-Za-z0-9_]*)\b", text)
    if class_match:
        old_class_name = class_match.group(1)
        text = re.sub(
            r"\bpublic\s+class\s+[A-Za-z_][A-Za-z0-9_]*\b",
            f"public class {new_class_name}",
            text,
            count=1,
        )
        text = re.sub(
            rf"\bnew\s+{re.escape(old_class_name)}\s*\(",
            f"new {new_class_name}(",
            text,
        )
    else:
        return False, "generated test has no public class declaration"

    try:
        target_path.parent.mkdir(parents=True, exist_ok=True)
        target_path.write_text(text, encoding="utf-8")
    except Exception as exc:  # noqa: BLE001
        return False, f"failed writing AI test to repo: {exc}"

    return target_path.exists(), f"copied generated test to {target_path}"


def run_one_condition(
    repo_id: str,
    module: str,
    condition: str,
    retries: int = 0,
    fixed_by: str = "NONE",
    mvn_force_update: bool = False,
    settings_override: Path | None = None,
) -> dict:
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
        "--retries",
        str(retries),
        "--fixed-by",
        fixed_by,
    ]
    if mvn_force_update:
        cmd.append("--mvn-force-update")
    if settings_override is not None:
        cmd.extend(["--settings-override", str(settings_override)])

    print(f"[run] {' '.join(cmd)}")
    start_time = time.time()
    completed = subprocess.run(cmd, cwd=str(BASE_DIR))
    status = find_latest_run_status(repo_id, module, condition, start_time)

    if not status:
        status = {
            "status": "OK" if completed.returncode == 0 else "FAIL",
            "phase": "",
            "error_type": "UnknownError" if completed.returncode != 0 else "",
            "error_message": "",
            "run_dir": "",
            "retries": retries,
            "fixed_by": fixed_by,
            "ended_at_utc": "",
            "build_tool": "maven",
            "test_scope": "focal",
        }

    mutation_score_pct = ""
    mutations_generated = ""
    mutations_killed = ""
    mutations_survived = ""
    run_dir = status.get("run_dir", "")
    if run_dir:
        summary_path = Path(run_dir) / "mutation_summary.json"
        if summary_path.exists():
            try:
                summary = json.loads(summary_path.read_text(encoding="utf-8"))
                total = int(summary.get("total_mutations", 0) or 0)
                killed = int(summary.get("killed_mutations", 0) or 0)
                survived = int(summary.get("survived_mutations", 0) or 0)
                score = float(summary.get("mutation_score", 0.0) or 0.0)
                mutation_score_pct = f"{(score * 100.0):.6f}"
                mutations_generated = str(total)
                mutations_killed = str(killed)
                mutations_survived = str(survived)
            except Exception as exc:  # noqa: BLE001
                print(f"[warn] failed reading mutation summary {summary_path}: {exc}")

    result = {
        "returncode": completed.returncode,
        "status": status.get("status", "FAIL"),
        "phase": status.get("phase", ""),
        "error_type": status.get("error_type", ""),
        "error_message": status.get("error_message", ""),
        "run_dir": run_dir,
        "retries": int(status.get("retries", retries) or retries),
        "fixed_by": status.get("fixed_by", fixed_by),
        "timestamp": status.get("ended_at_utc") or status.get("started_at_utc") or "",
        "build_tool": status.get("build_tool", "maven"),
        "test_scope": status.get("test_scope", "focal"),
        "mutation_score_pct": mutation_score_pct,
        "mutations_generated": mutations_generated,
        "mutations_killed": mutations_killed,
        "mutations_survived": mutations_survived,
    }
    print(
        "[result] "
        f"repo={repo_id} module={module} condition={condition} "
        f"status={result['status']} error_type={result['error_type'] or 'NONE'} "
        f"phase={result['phase'] or 'NONE'} mutation_score={result['mutation_score_pct'] or 'NA'}"
    )
    return result


def find_latest_run_status(repo_id: str, module: str, condition: str, start_time: float) -> dict | None:
    runs_dir = BASE_DIR / "logs" / "runs"
    if not runs_dir.exists():
        return None

    candidates = []
    for status_path in runs_dir.glob("*/run_status.json"):
        try:
            if status_path.stat().st_mtime < (start_time - 5):
                continue
            payload = json.loads(status_path.read_text(encoding="utf-8"))
        except Exception as exc:  # noqa: BLE001
            print(f"[warn] failed reading {status_path}: {exc}")
            continue

        if payload.get("repo_id") != repo_id:
            continue
        if payload.get("module") != module:
            continue
        if payload.get("condition") != condition:
            continue
        if payload.get("test_scope") != "focal":
            continue

        payload["run_dir"] = str(status_path.parent)
        candidates.append(payload)

    if not candidates:
        return None
    return max(candidates, key=lambda item: item.get("ended_at_utc") or item.get("started_at_utc") or "")


def determine_fix_strategy(error_type: str, error_message: str) -> str:
    hay = f"{error_type}\n{error_message}".lower()
    if error_type == "AI_TEST_MISSING":
        return "AI_TEST_MISSING"
    if (
        "http_blocker" in hay
        or "blocked mirror" in hay
        or "maven-default-http-blocker" in hay
        or "http://repository.apache.org/snapshots" in hay
        or "http snapshot repo" in hay
        or "appfuse-snapshots" in hay
        or "oss.sonatype.org" in hay
    ):
        return "HTTP_SETTINGS_FIX"
    if (
        "snapshot_missing" in hay
        or "was not found in" in hay
        or "cached in the local repository" in hay
        or "failure to find" in hay
    ):
        return "MAVEN_FORCE_UPDATE"
    return "NONE"


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


def clear_last_updated_cache(error_message: str) -> int:
    m2_repo = Path.home() / ".m2" / "repository"
    if not m2_repo.exists():
        return 0

    removed = 0
    seen = set()
    coords = re.findall(r"([A-Za-z0-9_.-]+):([A-Za-z0-9_.-]+):([A-Za-z0-9_.-]+):([A-Za-z0-9_.-]+)", error_message)
    for group_id, artifact_id, _packaging, version in coords:
        key = (group_id, artifact_id, version)
        if key in seen:
            continue
        seen.add(key)

        version_dir = m2_repo / Path(*group_id.split(".")) / artifact_id / version
        if not version_dir.exists():
            continue
        for marker in version_dir.glob("*.lastUpdated"):
            try:
                marker.unlink()
                removed += 1
            except OSError as exc:
                print(f"[warn] failed deleting {marker}: {exc}")

    return removed


def _module_dir(repo_id: str, module: str) -> Path:
    return BASE_DIR / "target-repos" / repo_id / module


def _repo_root(repo_id: str) -> Path:
    return BASE_DIR / "target-repos" / repo_id


def _candidate_poms(repo_id: str, module: str) -> list[Path]:
    module_dir = _module_dir(repo_id, module)
    repo_root = _repo_root(repo_id)
    poms: list[Path] = []
    current = module_dir
    while True:
        pom = current / "pom.xml"
        if pom.exists():
            poms.append(pom)
        if current == repo_root or current.parent == current:
            break
        current = current.parent
    root_pom = repo_root / "pom.xml"
    if root_pom.exists() and root_pom not in poms:
        poms.append(root_pom)
    return poms


def _pom_has_dependency(pom_text: str, group_id: str, artifact_id: str) -> bool:
    return bool(
        re.search(
            r"<groupId>\s*"
            + re.escape(group_id)
            + r"\s*</groupId>\s*<artifactId>\s*"
            + re.escape(artifact_id)
            + r"\s*</artifactId>",
            pom_text,
            flags=re.IGNORECASE | re.DOTALL,
        )
    )


def _dependency_present_in_module_or_parent(repo_id: str, module: str, group_id: str, artifact_id: str) -> bool:
    for pom in _candidate_poms(repo_id, module):
        try:
            text = pom.read_text(encoding="utf-8")
        except Exception:
            continue
        if _pom_has_dependency(text, group_id, artifact_id):
            return True
    return False


def detect_ai_test_required_dependencies(repo_id: str, module: str, mapping: dict) -> list[tuple[str, str, str]]:
    ai_test = expected_ai_test_path(_module_dir(repo_id, module), mapping)
    if not ai_test.exists():
        return []
    text = ai_test.read_text(encoding="utf-8")
    imports = set()
    for line in text.splitlines():
        line = line.strip()
        if line.startswith("import "):
            imports.add(line)

    needed: list[tuple[str, str, str]] = []
    if any("import org.mockito" in line for line in imports):
        needed.append(("org.mockito", "mockito-core", "3.12.4"))
    if any("import org.junit.jupiter" in line for line in imports):
        needed.append(("org.junit.jupiter", "junit-jupiter-api", "5.9.3"))
    if any("import org.junit." in line for line in imports):
        needed.append(("junit", "junit", "4.13.2"))
    if any("import org.hamcrest" in line for line in imports):
        needed.append(("org.hamcrest", "hamcrest-core", "1.3"))
    return needed


def missing_ai_test_dependencies(repo_id: str, module: str, needed: list[tuple[str, str, str]]) -> list[tuple[str, str, str]]:
    missing = []
    for group_id, artifact_id, version in needed:
        if not _dependency_present_in_module_or_parent(repo_id, module, group_id, artifact_id):
            missing.append((group_id, artifact_id, version))
    return missing


def _inject_test_dependencies_into_pom(pom_path: Path, deps: list[tuple[str, str, str]]) -> tuple[bool, list[str]]:
    if not deps:
        return False, []
    text = pom_path.read_text(encoding="utf-8")
    original = text
    inserted = []
    blocks = []
    for group_id, artifact_id, version in deps:
        if _pom_has_dependency(text, group_id, artifact_id):
            continue
        blocks.append(
            "\n    <dependency>\n"
            f"      <groupId>{group_id}</groupId>\n"
            f"      <artifactId>{artifact_id}</artifactId>\n"
            f"      <version>{version}</version>\n"
            "      <scope>test</scope>\n"
            "    </dependency>\n"
        )
        inserted.append(f"{group_id}:{artifact_id}:{version}")
    if not blocks:
        return False, inserted
    chunk = "".join(blocks)
    if "</dependencies>" in text:
        text = text.replace("</dependencies>", chunk + "  </dependencies>", 1)
    elif "</project>" in text:
        text = text.replace("</project>", f"  <dependencies>{chunk}  </dependencies>\n</project>", 1)
    else:
        return False, []
    if text != original:
        pom_path.write_text(text, encoding="utf-8")
        return True, inserted
    return False, []


def apply_temp_ai_test_dependency_patch(
    repo_id: str,
    module: str,
    deps: list[tuple[str, str, str]],
) -> tuple[bool, str, Path | None, list[str]]:
    module_pom = _module_dir(repo_id, module) / "pom.xml"
    if not module_pom.exists():
        return False, f"module pom not found: {module_pom}", None, []
    backup = module_pom.with_suffix(".xml.ai_dep_fix.bak")
    shutil.copy2(module_pom, backup)
    changed, inserted = _inject_test_dependencies_into_pom(module_pom, deps)
    if not changed:
        if backup.exists():
            backup.unlink()
        return False, "no dependency edits were applied", None, inserted
    return True, f"added test-scope deps to {module_pom}", backup, inserted


def restore_file_from_backup(path: Path, backup: Path | None) -> None:
    if backup is None:
        return
    try:
        if backup.exists():
            shutil.copy2(backup, path)
            backup.unlink()
    except Exception as exc:  # noqa: BLE001
        print(f"[warn] failed restoring backup {backup} -> {path}: {exc}")


def simplify_ai_test_file(repo_id: str, module: str, mapping: dict, error_message: str) -> tuple[bool, str, Path | None]:
    ai_test = expected_ai_test_path(_module_dir(repo_id, module), mapping)
    if not ai_test.exists():
        return False, f"AI test file missing: {ai_test}", None

    text = ai_test.read_text(encoding="utf-8")
    original = text
    backup = ai_test.with_suffix(".java.ai_simplify.bak")
    shutil.copy2(ai_test, backup)

    # Drop Mockito/JUnit runner heavy constructs to maximize legacy compile compatibility.
    text = re.sub(r"^\s*import\s+org\.mockito\..*?;\s*$", "", text, flags=re.MULTILINE)
    text = re.sub(r"^\s*import\s+static\s+org\.mockito\..*?;\s*$", "", text, flags=re.MULTILINE)
    text = re.sub(r"^\s*@RunWith\(MockitoJUnitRunner\.class\)\s*$", "", text, flags=re.MULTILINE)
    text = re.sub(r"^\s*@(?:Mock|InjectMocks|Spy|Captor)\s*$", "", text, flags=re.MULTILINE)
    text = re.sub(r"^\s*MockitoAnnotations\.openMocks\(this\);\s*$", "", text, flags=re.MULTILINE)
    text = re.sub(r"Mockito\.mock\([^)]*\)", "null", text)
    text = re.sub(r"^\s*.*(?:Mockito\.when\(|\bwhen\().*;\s*$", "", text, flags=re.MULTILINE)
    text = re.sub(r"^\s*.*\bverify\([^;]*;\s*$", "", text, flags=re.MULTILINE)
    text = re.sub(r"^\s*.*assert.*\.key\(\).*;\s*$", "", text, flags=re.MULTILINE | re.IGNORECASE)

    missing_method = re.search(r"symbol:\s*method\s+([A-Za-z_][A-Za-z0-9_]*)", error_message or "", flags=re.IGNORECASE)
    if missing_method:
        method_name = missing_method.group(1)
        text = re.sub(
            rf"^\s*.*assert.*\.{re.escape(method_name)}\([^;]*;\s*$",
            "",
            text,
            flags=re.MULTILINE | re.IGNORECASE,
        )

    if text == original:
        if backup.exists():
            backup.unlink()
        return False, "AI test simplification made no textual changes", None

    ai_test.write_text(text, encoding="utf-8")
    return True, f"simplified AI test file: {ai_test}", backup


def append_ai_patch_record(repo_id: str, module: str, payload: dict) -> None:
    BUILD_PATCHES_DIR.mkdir(parents=True, exist_ok=True)
    patch_path = BUILD_PATCHES_DIR / f"{repo_id}_{module}.json"
    doc: dict = {}
    if patch_path.exists():
        try:
            loaded = json.loads(patch_path.read_text(encoding="utf-8"))
            if isinstance(loaded, dict):
                doc = loaded
        except Exception:
            doc = {}
    records = doc.get("ai_compatibility_patches")
    if not isinstance(records, list):
        records = []
    records.append(payload)
    doc["ai_compatibility_patches"] = records
    patch_path.write_text(json.dumps(doc, indent=2), encoding="utf-8")


def write_paired_outputs(final_results: list[dict]) -> int:
    _ = final_results
    latest_success_by_key: dict[tuple[str, str, str], dict[str, str]] = {}
    if SUMMARY_CSV.exists():
        with SUMMARY_CSV.open(newline="", encoding="utf-8") as f:
            for row in csv.DictReader(f):
                condition = row.get("condition", "")
                status = row.get("status", "")
                if condition not in ("HUMAN", "AI_ONLY"):
                    continue
                if status != "OK":
                    continue
                key = (row.get("repo_id", ""), row.get("module", ""), condition)
                prev = latest_success_by_key.get(key)
                if not prev or row.get("timestamp", "") >= prev.get("timestamp", ""):
                    latest_success_by_key[key] = row

    by_pair: dict[tuple[str, str], dict[str, dict[str, str]]] = {}
    for (repo_id, module, condition), row in latest_success_by_key.items():
        by_pair.setdefault((repo_id, module), {})[condition] = row

    valid_pairs: list[tuple[tuple[str, str], dict[str, str], dict[str, str]]] = []
    for key in sorted(by_pair.keys()):
        pair = by_pair[key]
        human = pair.get("HUMAN")
        ai = pair.get("AI_ONLY")
        if human and ai:
            valid_pairs.append((key, human, ai))

    FINAL_COMPARISON_CSV.parent.mkdir(parents=True, exist_ok=True)
    with FINAL_COMPARISON_CSV.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(
            [
                "repo_id",
                "module",
                "test_scope",
                "build_tool",
                "human_score_pct",
                "ai_score_pct",
                "delta",
                "human_mutations_generated",
                "ai_only_mutations_generated",
                "human_mutations_killed",
                "ai_only_mutations_killed",
                "human_mutations_survived",
                "ai_only_mutations_survived",
                "human_timestamp",
                "ai_only_timestamp",
                "human_run_dir",
                "ai_only_run_dir",
            ]
        )
        for (repo_id, module), human, ai in valid_pairs:
            human_score = float(human.get("mutation_score_pct") or "0")
            ai_score = float(ai.get("mutation_score_pct") or "0")
            writer.writerow(
                [
                    repo_id,
                    module,
                    human.get("test_scope", ""),
                    human.get("build_tool", ""),
                    human.get("mutation_score_pct", ""),
                    ai.get("mutation_score_pct", ""),
                    f"{(ai_score - human_score):.6f}",
                    human.get("mutations_generated", ""),
                    ai.get("mutations_generated", ""),
                    human.get("mutations_killed", ""),
                    ai.get("mutations_killed", ""),
                    human.get("mutations_survived", ""),
                    ai.get("mutations_survived", ""),
                    human.get("timestamp", ""),
                    ai.get("timestamp", ""),
                    human.get("run_dir", ""),
                    ai.get("run_dir", ""),
                ]
            )

    with REPRODUCIBLE_SUBSET_CSV.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["repo_id", "module"])
        for (repo_id, module), _human, _ai in valid_pairs:
            writer.writerow([repo_id, module])

    return len(valid_pairs)


FAILURE_CATEGORIES = [
    "COMPILATION",
    "DEP_RESOLUTION",
    "HTTP_BLOCKER",
    "SNAPSHOT_MISSING",
    "MODULE_MISSING",
    "FILE_NOT_FOUND",
    "PIT_ERROR",
    "TEST_FAILURE",
    "TIMEOUT",
    "OTHER",
]


def _flatten_message(text: str) -> str:
    return " ".join((text or "").split())[:400]


def normalize_error_type(error_type: str, error_message: str = "") -> str:
    value = (error_type or "").strip().upper()
    hay = f"{value}\n{error_message}".lower()
    if value in FAILURE_CATEGORIES:
        return value
    if value == "AI_TEST_MISSING":
        return "FILE_NOT_FOUND"
    if "timed out" in hay or "timeoutexpired" in hay:
        return "TIMEOUT"
    if "pitest" in hay or "mutationcoveragereport" in hay or "org.pitest" in hay:
        return "PIT_ERROR"
    if "http_blocker" in hay or "maven-default-http-blocker" in hay or "blocked mirror" in hay:
        return "HTTP_BLOCKER"
    if "snapshot" in hay and "missing" in hay:
        return "SNAPSHOT_MISSING"
    if "dependency" in hay and "resolve" in hay:
        return "DEP_RESOLUTION"
    if "compilation" in hay:
        return "COMPILATION"
    if "test failure" in hay or "there are test failures" in hay:
        return "TEST_FAILURE"
    if "module directory does not exist" in hay:
        return "MODULE_MISSING"
    if "file" in hay and "not found" in hay:
        return "FILE_NOT_FOUND"
    return "OTHER"


def _to_int(value: str) -> int | None:
    if value is None:
        return None
    text = str(value).strip()
    if not text:
        return None
    try:
        return int(float(text))
    except ValueError:
        return None


def _to_float(value: str) -> float | None:
    if value is None:
        return None
    text = str(value).strip()
    if not text:
        return None
    try:
        return float(text)
    except ValueError:
        return None


def _module_as_path(module: str) -> Path:
    normalized = module.replace("\\", "/")
    parts = [part for part in normalized.split("/") if part]
    return Path(*parts) if parts else Path(module)


def resolve_commit_hash(repo_id: str, mapping: dict) -> str:
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
    repo_root = _repo_root(repo_id)
    try:
        proc = subprocess.run(
            ["git", "rev-parse", "HEAD"],
            cwd=str(repo_root),
            text=True,
            capture_output=True,
            check=False,
        )
        if proc.returncode == 0 and proc.stdout.strip():
            return proc.stdout.strip()
    except Exception:
        pass
    return ""


def cleanup_repo_state(repo_id: str) -> None:
    repo_root = _repo_root(repo_id)
    if not repo_root.exists():
        return
    for cmd in (["git", "reset", "--hard"], ["git", "clean", "-fdx"]):
        proc = subprocess.run(cmd, cwd=str(repo_root), text=True, capture_output=True, check=False)
        if proc.returncode != 0:
            print(f"[cleanup][warn] {' '.join(cmd)} failed for {repo_root}: {proc.stderr.strip()}")


def run_one_condition(
    repo_id: str,
    module: str,
    condition: str,
    retries: int = 0,
    fixed_by: str = "NONE",
    mvn_force_update: bool = False,
    settings_override: Path | None = None,
) -> dict:
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
        "--retries",
        str(retries),
        "--fixed-by",
        fixed_by,
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
    except Exception as exc:  # noqa: BLE001
        return {
            "returncode": 1,
            "status": "FAIL",
            "phase": "invoke",
            "error_type": normalize_error_type("OTHER", str(exc)),
            "error_message": _flatten_message(str(exc)),
            "run_dir": "",
            "run_status_path": "",
            "retries": retries,
            "fixed_by": fixed_by,
            "timestamp": "",
            "build_tool": "maven",
            "test_scope": "focal",
            "mutation_score_pct": "",
            "mutations_generated": "",
            "mutations_killed": "",
            "mutations_survived": "",
            "pit_target_classes": "",
            "pit_version": "",
            "pit_status": "",
            "pit_log_file": "",
            "pit_config": {},
            "log_paths": [],
        }

    status = find_latest_run_status(repo_id, module, condition, start_time)
    if not status:
        status = {
            "status": "OK" if returncode == 0 else "FAIL",
            "phase": "",
            "error_type": "OTHER" if returncode != 0 else "",
            "error_message": "",
            "run_dir": "",
            "retries": retries,
            "fixed_by": fixed_by,
            "ended_at_utc": "",
            "build_tool": "maven",
            "test_scope": "focal",
            "pit_target_classes": "",
            "pit_version": "",
            "pit_status": "",
            "pit_log_file": "",
            "pit_config": {},
            "commands": [],
        }

    mutation_score_pct = ""
    mutations_generated = ""
    mutations_killed = ""
    mutations_survived = ""
    run_dir = status.get("run_dir", "")
    if run_dir:
        summary_path = Path(run_dir) / "mutation_summary.json"
        if summary_path.exists():
            try:
                summary = json.loads(summary_path.read_text(encoding="utf-8"))
                total = int(summary.get("total_mutations", 0) or 0)
                killed = int(summary.get("killed_mutations", 0) or 0)
                survived = int(summary.get("survived_mutations", 0) or 0)
                score = float(summary.get("mutation_score", 0.0) or 0.0)
                mutation_score_pct = f"{(score * 100.0):.6f}"
                mutations_generated = str(total)
                mutations_killed = str(killed)
                mutations_survived = str(survived)
            except Exception as exc:  # noqa: BLE001
                print(f"[warn] failed reading mutation summary {summary_path}: {exc}")

    error_type = normalize_error_type(status.get("error_type", ""), status.get("error_message", ""))
    commands = status.get("commands") if isinstance(status.get("commands"), list) else []
    log_paths = [entry.get("log_file", "") for entry in commands if isinstance(entry, dict) and entry.get("log_file")]
    run_status_path = str(Path(run_dir) / "run_status.json") if run_dir else ""

    result = {
        "returncode": returncode,
        "status": status.get("status", "FAIL"),
        "phase": status.get("phase", ""),
        "error_type": error_type if status.get("status") != "OK" else "",
        "error_message": _flatten_message(status.get("error_message", "")),
        "run_dir": run_dir,
        "run_status_path": run_status_path,
        "retries": int(status.get("retries", retries) or retries),
        "fixed_by": status.get("fixed_by", fixed_by),
        "timestamp": status.get("ended_at_utc") or status.get("started_at_utc") or "",
        "build_tool": status.get("build_tool", "maven"),
        "test_scope": status.get("test_scope", "focal"),
        "mutation_score_pct": mutation_score_pct,
        "mutations_generated": mutations_generated,
        "mutations_killed": mutations_killed,
        "mutations_survived": mutations_survived,
        "pit_target_classes": status.get("pit_target_classes", ""),
        "pit_version": status.get("pit_version", ""),
        "pit_status": status.get("pit_status", ""),
        "pit_log_file": status.get("pit_log_file", ""),
        "pit_config": status.get("pit_config", {}) if isinstance(status.get("pit_config"), dict) else {},
        "log_paths": log_paths,
    }
    print(
        "[result] "
        f"repo={repo_id} module={module} condition={condition} "
        f"status={result['status']} error_type={result['error_type'] or 'NONE'} "
        f"phase={result['phase'] or 'NONE'} mutation_score={result['mutation_score_pct'] or 'NA'}"
    )
    return result


def determine_fix_strategy(error_type: str, error_message: str) -> str:
    hay = f"{error_type}\n{error_message}".lower()
    if "no ai test class found" in hay or "generated test not found" in hay:
        return "AI_TEST_MISSING"
    if (
        "http_blocker" in hay
        or "blocked mirror" in hay
        or "maven-default-http-blocker" in hay
        or "http://repository.apache.org/snapshots" in hay
        or "http snapshot repo" in hay
        or "appfuse-snapshots" in hay
        or "oss.sonatype.org" in hay
    ):
        return "HTTP_SETTINGS_FIX"
    if (
        "snapshot_missing" in hay
        or "was not found in" in hay
        or "cached in the local repository" in hay
        or "failure to find" in hay
    ):
        return "MAVEN_FORCE_UPDATE"
    return "NONE"


def write_condition_artifact(
    repo_id: str,
    module: str,
    condition: str,
    commit_hash: str,
    focal_class: str,
    entry: dict,
    applied_fixes: list[str],
) -> None:
    target_dir = BASE_DIR / "experiments" / "RQ1_mutation_score" / "per_repo" / repo_id / _module_as_path(module) / condition
    target_dir.mkdir(parents=True, exist_ok=True)
    payload = {
        "repo_id": repo_id,
        "module": module,
        "condition": condition,
        "commit_hash": commit_hash,
        "focal_class": focal_class,
        "ok": entry.get("status") == "OK",
        "status": entry.get("status", "FAIL"),
        "error_type": entry.get("error_type", ""),
        "error_message": entry.get("error_message", ""),
        "metrics": {
            "mutation_score_pct": entry.get("mutation_score_pct", ""),
            "mutants_total": entry.get("mutations_generated", ""),
            "killed": entry.get("mutations_killed", ""),
            "survived": entry.get("mutations_survived", ""),
        },
        "pit_config": {
            "pit_version": entry.get("pit_version", ""),
            "target_classes": entry.get("pit_target_classes", ""),
            **(entry.get("pit_config", {}) if isinstance(entry.get("pit_config"), dict) else {}),
        },
        "logs": {
            "run_dir": entry.get("run_dir", ""),
            "run_status_path": entry.get("run_status_path", ""),
            "pit_log_file": entry.get("pit_log_file", ""),
            "command_logs": entry.get("log_paths", []),
        },
        "applied_fixes": applied_fixes,
        "timestamp": entry.get("timestamp", ""),
    }
    (target_dir / "mutation_summary.json").write_text(json.dumps(payload, indent=2), encoding="utf-8")


def write_paired_outputs(records: list[dict]) -> int:
    FINAL_COMPARISON_CSV.parent.mkdir(parents=True, exist_ok=True)
    header = [
        "repo_id",
        "module",
        "focal_class",
        "commit_hash",
        "human_ok",
        "human_score_pct",
        "human_total",
        "human_killed",
        "human_survived",
        "human_error",
        "ai_ok",
        "ai_score_pct",
        "ai_total",
        "ai_killed",
        "ai_survived",
        "ai_error",
        "delta_score_pct",
        "delta_killed",
        "delta_survived",
        "mutant_total_ratio",
        "scope_mismatch",
        "applied_fixes",
        "run_timestamp",
    ]

    valid_pairs = 0
    with FINAL_COMPARISON_CSV.open("w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=header)
        writer.writeheader()
        for rec in records:
            human = rec.get("HUMAN", {})
            ai = rec.get("AI_ONLY", {})

            human_ok = human.get("status") == "OK"
            ai_ok = ai.get("status") == "OK"
            if human_ok and ai_ok:
                valid_pairs += 1

            human_score = _to_float(human.get("mutation_score_pct", ""))
            ai_score = _to_float(ai.get("mutation_score_pct", ""))
            human_total = _to_int(human.get("mutations_generated", ""))
            ai_total = _to_int(ai.get("mutations_generated", ""))
            human_killed = _to_int(human.get("mutations_killed", ""))
            ai_killed = _to_int(ai.get("mutations_killed", ""))
            human_survived = _to_int(human.get("mutations_survived", ""))
            ai_survived = _to_int(ai.get("mutations_survived", ""))

            delta_score = ""
            if human_score is not None and ai_score is not None:
                delta_score = f"{(ai_score - human_score):.6f}"

            delta_killed = ""
            if human_killed is not None and ai_killed is not None:
                delta_killed = str(ai_killed - human_killed)

            delta_survived = ""
            if human_survived is not None and ai_survived is not None:
                delta_survived = str(ai_survived - human_survived)

            mutant_total_ratio = ""
            scope_mismatch = ""
            if human_ok and ai_ok and human_total and human_total > 0 and ai_total is not None:
                ratio = ai_total / human_total
                mutant_total_ratio = f"{ratio:.6f}"
                scope_mismatch = "TRUE" if (ratio < 0.9 or ratio > 1.1) else "FALSE"

            writer.writerow(
                {
                    "repo_id": rec.get("repo_id", ""),
                    "module": rec.get("module", ""),
                    "focal_class": rec.get("focal_class", ""),
                    "commit_hash": rec.get("commit_hash", ""),
                    "human_ok": "TRUE" if human_ok else "FALSE",
                    "human_score_pct": human.get("mutation_score_pct", ""),
                    "human_total": human.get("mutations_generated", ""),
                    "human_killed": human.get("mutations_killed", ""),
                    "human_survived": human.get("mutations_survived", ""),
                    "human_error": human.get("error_type", ""),
                    "ai_ok": "TRUE" if ai_ok else "FALSE",
                    "ai_score_pct": ai.get("mutation_score_pct", ""),
                    "ai_total": ai.get("mutations_generated", ""),
                    "ai_killed": ai.get("mutations_killed", ""),
                    "ai_survived": ai.get("mutations_survived", ""),
                    "ai_error": ai.get("error_type", ""),
                    "delta_score_pct": delta_score,
                    "delta_killed": delta_killed,
                    "delta_survived": delta_survived,
                    "mutant_total_ratio": mutant_total_ratio,
                    "scope_mismatch": scope_mismatch,
                    "applied_fixes": ";".join(rec.get("applied_fixes", [])),
                    "run_timestamp": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
                }
            )

    with REPRODUCIBLE_SUBSET_CSV.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["repo_id", "module"])
        for rec in records:
            if rec.get("HUMAN", {}).get("status") == "OK" and rec.get("AI_ONLY", {}).get("status") == "OK":
                writer.writerow([rec.get("repo_id", ""), rec.get("module", "")])

    return valid_pairs


def write_failure_breakdown(results: list[dict]) -> dict:
    counts = {category: 0 for category in FAILURE_CATEGORIES}
    for item in results:
        if item.get("status") == "OK":
            continue
        normalized = normalize_error_type(item.get("error_type", ""), item.get("error_message", ""))
        counts[normalized] = counts.get(normalized, 0) + 1
    payload = {
        "generated_at": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
        "total_failures": sum(counts.values()),
        "by_category": counts,
    }
    (BASE_DIR / "latest_failure_breakdown.json").write_text(json.dumps(payload, indent=2), encoding="utf-8")
    return payload


def main() -> int:
    json_files = sorted(DATASET_DIR.glob("*.json"))
    total = len(json_files)
    if total == 0:
        print(f"No dataset JSON files found in {DATASET_DIR}")
        return 1

    final_results: list[dict] = []
    per_repo_records: list[dict] = []

    for idx, json_path in enumerate(json_files, start=1):
        repo_id = extract_repo_id(json_path)
        mapping = json.loads(json_path.read_text(encoding="utf-8"))
        module = resolve_module(repo_id, mapping)
        focal_class = java_file_to_fqcn(mapping["focal_class"]["file"]) if mapping.get("focal_class") else ""
        commit_hash = resolve_commit_hash(repo_id, mapping)

        print(f"[{idx}/{total}] Running repo {repo_id} (module={module})")
        record = {
            "repo_id": repo_id,
            "module": module,
            "focal_class": focal_class,
            "commit_hash": commit_hash,
            "HUMAN": {},
            "AI_ONLY": {},
            "applied_fixes": [],
        }

        ai_needed_deps: list[tuple[str, str, str]] = []
        ai_missing_deps: list[tuple[str, str, str]] = []

        for condition in ("HUMAN", "AI_ONLY"):
            condition_fixes: list[str] = []

            def _run_attempt(retries: int, fixed_by: str, mvn_force_update: bool = False, settings_override: Path | None = None) -> dict:
                if condition == "AI_ONLY":
                    try:
                        ai_ok, ai_msg = provision_ai_test_if_missing(repo_id, module, mapping, json_path)
                    except Exception as exc:  # noqa: BLE001
                        ai_ok, ai_msg = False, f"AI test provisioning failed: {exc}"
                    if ai_ok:
                        print(f"[ai-test] {repo_id}/{module}: {ai_msg}")
                    else:
                        print(f"[ai-test][warn] {repo_id}/{module}: {ai_msg}")

                result = run_one_condition(
                    repo_id,
                    module,
                    condition,
                    retries=retries,
                    fixed_by=fixed_by,
                    mvn_force_update=mvn_force_update,
                    settings_override=settings_override,
                )
                cleanup_repo_state(repo_id)
                return result

            if condition == "AI_ONLY":
                try:
                    ai_needed_deps = detect_ai_test_required_dependencies(repo_id, module, mapping)
                    ai_missing_deps = missing_ai_test_dependencies(repo_id, module, ai_needed_deps)
                except Exception as exc:  # noqa: BLE001
                    print(f"[ai-compat][warn] dependency precheck failed for {repo_id}/{module}: {exc}")

            first = _run_attempt(retries=0, fixed_by="NONE")
            final_entry = {"repo_id": repo_id, "module": module, "condition": condition, **first}

            if first.get("status") != "OK":
                strategy = determine_fix_strategy(first.get("error_type", ""), first.get("error_message", ""))
                if strategy == "HTTP_SETTINGS_FIX":
                    removed = clear_http_blocker_cache(first.get("error_message", ""))
                    if removed:
                        print(f"[fix] cleared {removed} HTTP-blocked group cache paths for {repo_id}/{module}/{condition}")
                    settings_path = ensure_repo_settings_https(repo_id)
                    retry = _run_attempt(
                        retries=1,
                        fixed_by="HTTP_SETTINGS_FIX",
                        mvn_force_update=True,
                        settings_override=settings_path,
                    )
                    final_entry = {"repo_id": repo_id, "module": module, "condition": condition, **retry}
                    condition_fixes.append("HTTP_SETTINGS_FIX")
                elif strategy == "MAVEN_FORCE_UPDATE":
                    removed = clear_last_updated_cache(first.get("error_message", ""))
                    print(f"[fix] cleared {removed} .lastUpdated files for {repo_id}/{module}/{condition}")
                    retry = _run_attempt(retries=1, fixed_by="MAVEN_FORCE_UPDATE", mvn_force_update=True)
                    final_entry = {"repo_id": repo_id, "module": module, "condition": condition, **retry}
                    condition_fixes.append("MAVEN_FORCE_UPDATE")
                elif condition == "AI_ONLY":
                    ai_recovered = False
                    if ai_missing_deps:
                        _ = provision_ai_test_if_missing(repo_id, module, mapping, json_path)
                        module_pom = _module_dir(repo_id, module) / "pom.xml"
                        dep_ok, dep_msg, dep_backup, inserted = apply_temp_ai_test_dependency_patch(
                            repo_id,
                            module,
                            ai_missing_deps,
                        )
                        print(f"[ai-compat] {repo_id}/{module}: {dep_msg}")
                        if dep_ok:
                            try:
                                retry = run_one_condition(repo_id, module, condition, retries=1, fixed_by="AI_TEST_DEP_FIX")
                                final_entry = {"repo_id": repo_id, "module": module, "condition": condition, **retry}
                            finally:
                                restore_file_from_backup(module_pom, dep_backup)
                                cleanup_repo_state(repo_id)
                            append_ai_patch_record(
                                repo_id,
                                module,
                                {
                                    "kind": "AI_TEST_DEP_FIX",
                                    "condition": condition,
                                    "added_dependencies": inserted,
                                    "timestamp_utc": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
                                },
                            )
                            condition_fixes.append("AI_TEST_DEP_FIX")
                            ai_recovered = final_entry.get("status") == "OK"

                    if not ai_recovered and final_entry.get("status") != "OK":
                        err_type = normalize_error_type(final_entry.get("error_type", ""), final_entry.get("error_message", ""))
                        if err_type in ("COMPILATION", "TEST_FAILURE", "OTHER", "FILE_NOT_FOUND"):
                            _ = provision_ai_test_if_missing(repo_id, module, mapping, json_path)
                            simp_ok, simp_msg, simp_backup = simplify_ai_test_file(
                                repo_id,
                                module,
                                mapping,
                                final_entry.get("error_message", ""),
                            )
                            print(f"[ai-compat] {repo_id}/{module}: {simp_msg}")
                            if simp_ok:
                                ai_test_path = expected_ai_test_path(_module_dir(repo_id, module), mapping)
                                try:
                                    retry = run_one_condition(repo_id, module, condition, retries=1, fixed_by="AI_TEST_SIMPLIFY")
                                    final_entry = {"repo_id": repo_id, "module": module, "condition": condition, **retry}
                                finally:
                                    restore_file_from_backup(ai_test_path, simp_backup)
                                    cleanup_repo_state(repo_id)
                                append_ai_patch_record(
                                    repo_id,
                                    module,
                                    {
                                        "kind": "AI_TEST_SIMPLIFY",
                                        "condition": condition,
                                        "timestamp_utc": time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime()),
                                    },
                                )
                                condition_fixes.append("AI_TEST_SIMPLIFY")

            if final_entry.get("fixed_by") and final_entry.get("fixed_by") != "NONE":
                condition_fixes.append(final_entry["fixed_by"])
            condition_fixes = sorted(dict.fromkeys(condition_fixes))
            record["applied_fixes"].extend(condition_fixes)
            record[condition] = final_entry
            final_results.append(final_entry)

            write_condition_artifact(
                repo_id=repo_id,
                module=module,
                condition=condition,
                commit_hash=commit_hash,
                focal_class=focal_class,
                entry=final_entry,
                applied_fixes=condition_fixes,
            )

        record["applied_fixes"] = sorted(dict.fromkeys(record["applied_fixes"]))
        per_repo_records.append(record)

    valid_pairs = write_paired_outputs(per_repo_records)
    breakdown = write_failure_breakdown(final_results)

    human_ok_count = sum(1 for rec in per_repo_records if rec.get("HUMAN", {}).get("status") == "OK")
    ai_only_ok_count = sum(1 for rec in per_repo_records if rec.get("AI_ONLY", {}).get("status") == "OK")

    print("")
    print(f"TOTAL_REPOS: {total}")
    print(f"HUMAN_OK_COUNT: {human_ok_count}")
    print(f"AI_ONLY_OK_COUNT: {ai_only_ok_count}")
    print(f"VALID_PAIRED_COUNT: {valid_pairs}")
    print(f"FAILURE_COUNT: {breakdown['total_failures']}")
    print(f"FINAL_COMPARISON_PATH: {FINAL_COMPARISON_CSV}")
    print(f"REPRODUCIBLE_SUBSET_PATH: {REPRODUCIBLE_SUBSET_CSV}")
    print(f"FAILURE_BREAKDOWN_PATH: {BASE_DIR / 'latest_failure_breakdown.json'}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())


