import hashlib
import json
import re
import shutil
import subprocess
import zipfile
from dataclasses import asdict, dataclass, field
from datetime import datetime, timezone
from pathlib import Path
from typing import Any
from urllib.error import URLError
from urllib.request import urlretrieve


MAVEN_SETTINGS_CONTENT = """<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd">

  <mirrors>
    <mirror>
      <id>central</id>
      <mirrorOf>central</mirrorOf>
      <url>https://repo1.maven.org/maven2</url>
    </mirror>
    <mirror>
      <id>jboss-https</id>
      <mirrorOf>JBOSS</mirrorOf>
      <url>https://repository.jboss.org/nexus/content/groups/public/</url>
    </mirror>
    <mirror>
      <id>geotools-https</id>
      <mirrorOf>Geotools</mirrorOf>
      <url>https://repo.osgeo.org/repository/release/</url>
    </mirror>
  </mirrors>

</settings>
"""

JACKSUM_VERSION = "1.7.0"
JACKSUM_DOWNLOAD_URLS = [
    f"https://repo1.maven.org/maven2/jonelo/jacksum/{JACKSUM_VERSION}/jacksum-{JACKSUM_VERSION}.jar",
    f"https://repo.maven.apache.org/maven2/jonelo/jacksum/{JACKSUM_VERSION}/jacksum-{JACKSUM_VERSION}.jar",
]
JACKSUM_ZIP_URL = f"https://sourceforge.net/projects/jacksum/files/jacksum/jacksum-{JACKSUM_VERSION}.zip/download"


@dataclass
class PatchReport:
    repo_id: str
    repo_root: str
    module_dir: str
    settings_path: str | None = None
    pom_path: str | None = None
    backup_path: str | None = None
    changes: list[str] = field(default_factory=list)
    removed_dependencies: list[str] = field(default_factory=list)
    installed_jars: list[dict[str, Any]] = field(default_factory=list)
    commands: list[list[str]] = field(default_factory=list)
    checksums: dict[str, dict[str, Any]] = field(default_factory=dict)
    generated_at_utc: str = field(
        default_factory=lambda: datetime.now(timezone.utc).isoformat()
    )

    def to_json_dict(self) -> dict[str, Any]:
        return asdict(self)


def _sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def _record_checksum(report: PatchReport, path: Path) -> None:
    if path.exists():
        report.checksums[str(path)] = {
            "sha256": _sha256(path),
            "size_bytes": path.stat().st_size,
        }


def ensure_maven_settings(root_dir: Path) -> Path:
    root_dir = Path(root_dir)
    settings_path = root_dir / ".mvn" / "settings.xml"
    settings_path.parent.mkdir(parents=True, exist_ok=True)
    existing = settings_path.read_text(encoding="utf-8") if settings_path.exists() else ""
    if existing != MAVEN_SETTINGS_CONTENT:
        settings_path.write_text(MAVEN_SETTINGS_CONTENT, encoding="utf-8")
    return settings_path


def _remove_dependency_block(pom_text: str, group_id: str, artifact_id: str) -> tuple[str, bool]:
    pattern = re.compile(
        r"<dependency>\s*"
        r"<groupId>\s*" + re.escape(group_id) + r"\s*</groupId>\s*"
        r"<artifactId>\s*" + re.escape(artifact_id) + r"\s*</artifactId>"
        r"[\s\S]*?"
        r"</dependency>\s*",
        re.MULTILINE,
    )
    new_text, count = pattern.subn("", pom_text)
    return new_text, count > 0


def _ensure_jacksum_dependency(pom_text: str) -> tuple[str, bool]:
    has_jacksum = bool(
        re.search(
            r"<groupId>\s*jacksum\s*</groupId>\s*<artifactId>\s*jacksum\s*</artifactId>",
            pom_text,
            re.MULTILINE,
        )
    )
    if has_jacksum:
        return pom_text, False

    insertion = (
        "\n\t\t<dependency>\n"
        "\t\t\t<groupId>jacksum</groupId>\n"
        "\t\t\t<artifactId>jacksum</artifactId>\n"
        f"\t\t\t<version>{JACKSUM_VERSION}</version>\n"
        "\t\t</dependency>\n"
    )
    marker = "<!-- Test -->"
    idx = pom_text.find(marker)
    if idx == -1:
        raise RuntimeError("Could not find insertion marker for jacksum dependency in pom.xml")
    return pom_text[:idx] + insertion + pom_text[idx:], True


def _patch_bootstrap_classloader(module_dir: Path) -> bool:
    bootstrap_path = module_dir / "src" / "main" / "java" / "org" / "red5" / "server" / "Bootstrap.java"
    if not bootstrap_path.exists():
        return False
    text = bootstrap_path.read_text(encoding="utf-8")
    original = text

    text = text.replace(
        "import org.red5.classloading.ClassLoaderBuilder;\n",
        "",
    )
    text = text.replace(
        "\t\t// pass urls to the ClassLoader\n\t\tClassLoader loader = ClassLoaderBuilder.build(null, ClassLoaderBuilder.USE_RED5_LIB, null);\n",
        (
            "\t\t// Try legacy ClassLoaderBuilder if present; otherwise keep context loader.\n"
            "\t\tClassLoader loader = Thread.currentThread().getContextClassLoader();\n"
            "\t\ttry {\n"
            '\t\t\tClass<?> builder = Class.forName("org.red5.classloading.ClassLoaderBuilder");\n'
            '\t\t\tMethod buildMethod = null;\n'
            "\t\t\tfor (Method method : builder.getMethods()) {\n"
            '\t\t\t\tif ("build".equals(method.getName()) && method.getParameterTypes().length == 3) {\n'
            "\t\t\t\t\tbuildMethod = method;\n"
            "\t\t\t\t\tbreak;\n"
            "\t\t\t\t}\n"
            "\t\t\t}\n"
            "\t\t\tif (buildMethod != null) {\n"
            '\t\t\t\tNumber mode = (Number) builder.getField("USE_RED5_LIB").get(null);\n'
            "\t\t\t\tObject candidate = buildMethod.invoke(null, new Object[] { null, mode.intValue(), null });\n"
            "\t\t\t\tif (candidate instanceof ClassLoader) {\n"
            "\t\t\t\t\tloader = (ClassLoader) candidate;\n"
            "\t\t\t\t}\n"
            "\t\t\t}\n"
            "\t\t} catch (Throwable t) {\n"
            "\t\t\t// Keep default context classloader when the legacy builder is absent.\n"
            "\t\t}\n"
        ),
    )

    if text != original:
        bootstrap_path.write_text(text, encoding="utf-8")
        return True
    return False


def patch_red5_base(repo_root: Path, remove_jmx: bool = True) -> PatchReport:
    repo_root = Path(repo_root)
    module_dir = repo_root / "red5_base" if (repo_root / "red5_base").exists() else repo_root
    repo_id = repo_root.name
    report = PatchReport(
        repo_id=repo_id,
        repo_root=str(repo_root),
        module_dir=str(module_dir),
    )

    pom_path = module_dir / "pom.xml"
    bak_path = module_dir / "pom.xml.bak"
    report.pom_path = str(pom_path)
    report.backup_path = str(bak_path)

    if not pom_path.exists():
        raise FileNotFoundError(f"Missing pom.xml: {pom_path}")
    if not bak_path.exists():
        shutil.copy2(pom_path, bak_path)
        report.changes.append("Created deterministic backup pom.xml.bak")

    pom_text = pom_path.read_text(encoding="utf-8")
    original_text = pom_text

    pom_text = pom_text.replace(
        "http://repository.jboss.org/maven2",
        "https://repository.jboss.org/nexus/content/groups/public/",
    )
    pom_text = pom_text.replace(
        "http://maven.geotools.fr/repository",
        "https://repo.osgeo.org/repository/release/",
    )
    if pom_text != original_text:
        report.changes.append("Updated legacy HTTP repository URLs to HTTPS")

    pom_text, added_jacksum = _ensure_jacksum_dependency(pom_text)
    if added_jacksum:
        report.changes.append("Added jacksum:jacksum:1.7.0 dependency")

    if remove_jmx:
        pom_text, removed_jmxremote = _remove_dependency_block(
            pom_text, "javax.management", "jmxremote"
        )
        pom_text, removed_jmxtools = _remove_dependency_block(
            pom_text, "com.sun.jdmk", "jmxtools"
        )
        if removed_jmxremote:
            report.removed_dependencies.append("javax.management:jmxremote:1.0.1")
        if removed_jmxtools:
            report.removed_dependencies.append("com.sun.jdmk:jmxtools:1.2.1")
        if removed_jmxremote or removed_jmxtools:
            report.changes.append("Removed legacy JMX dependencies")

    if pom_text != original_text:
        pom_path.write_text(pom_text, encoding="utf-8")

    if _patch_bootstrap_classloader(module_dir):
        report.changes.append("Patched Bootstrap.java to avoid hard dependency on ClassLoaderBuilder")

    _record_checksum(report, bak_path)
    _record_checksum(report, pom_path)
    return report


def _run_command(cmd: list[str], cwd: Path, report: PatchReport) -> None:
    report.commands.append(cmd)
    subprocess.run(cmd, cwd=str(cwd), check=True)


def install_missing_jars(
    workspace_root: Path,
    settings_path: Path,
    report: PatchReport,
    mvn_cmd: str = "mvn.cmd",
) -> None:
    workspace_root = Path(workspace_root)
    settings_path = Path(settings_path)
    jars_dir = workspace_root / "experiments" / "third_party_jars"
    jars_dir.mkdir(parents=True, exist_ok=True)
    jacksum_jar = jars_dir / f"jacksum-{JACKSUM_VERSION}.jar"

    if not jacksum_jar.exists():
        download_errors: list[str] = []
        for url in JACKSUM_DOWNLOAD_URLS:
            try:
                urlretrieve(url, jacksum_jar)
                report.changes.append(f"Downloaded jacksum jar from {url}")
                break
            except URLError as exc:
                download_errors.append(f"{url}: {exc}")
        if not jacksum_jar.exists():
            zip_path = jars_dir / f"jacksum-{JACKSUM_VERSION}.zip"
            try:
                urlretrieve(JACKSUM_ZIP_URL, zip_path)
                with zipfile.ZipFile(zip_path) as zf:
                    with zf.open("jacksum.jar") as src, jacksum_jar.open("wb") as dst:
                        shutil.copyfileobj(src, dst)
                report.changes.append(f"Downloaded jacksum zip from {JACKSUM_ZIP_URL}")
                report.changes.append("Extracted jacksum.jar from zip archive")
                _record_checksum(report, zip_path)
            except Exception as exc:  # noqa: BLE001
                download_errors.append(f"{JACKSUM_ZIP_URL}: {exc}")
        if not jacksum_jar.exists():
            raise RuntimeError(
                "Unable to download jacksum jar from configured URLs:\n" + "\n".join(download_errors)
            )

    checksum = _sha256(jacksum_jar)
    report.installed_jars.append(
        {
            "path": str(jacksum_jar),
            "sha256": checksum,
            "size_bytes": jacksum_jar.stat().st_size,
            "coordinates": "jacksum:jacksum:1.7.0",
        }
    )
    _record_checksum(report, jacksum_jar)

    install_cmd = [
        mvn_cmd,
        "-s",
        str(settings_path),
        "install:install-file",
        f"-Dfile={jacksum_jar}",
        "-DgroupId=jacksum",
        "-DartifactId=jacksum",
        f"-Dversion={JACKSUM_VERSION}",
        "-Dpackaging=jar",
        "-DgeneratePom=true",
    ]
    _run_command(install_cmd, cwd=workspace_root, report=report)


def write_patch_report(report: PatchReport, out_path: Path) -> Path:
    out_path = Path(out_path)
    out_path.parent.mkdir(parents=True, exist_ok=True)
    out_path.write_text(json.dumps(report.to_json_dict(), indent=2), encoding="utf-8")
    return out_path
