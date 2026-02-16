import json
import os
import xml.etree.ElementTree as ET
from pathlib import Path

XML_PATH = Path("target/pit-reports/mutations.xml")
OUT_DIR = Path("artifacts")
OUT_PATH = OUT_DIR / "mutation_summary.json"

def main():
    if not XML_PATH.exists():
        raise SystemExit(f"Missing {XML_PATH}")

    tree = ET.parse(XML_PATH)
    root = tree.getroot()

    muts = root.findall("mutation")
    total = len(muts)

    counts = {"total": total, "killed": 0, "survived": 0, "noCoverage": 0, "timedOut": 0, "nonViable": 0, "memoryError": 0}
    problems = []

    for m in muts:
        status = m.attrib.get("status", "")
        if status == "KILLED":
            counts["killed"] += 1
        elif status == "SURVIVED":
            counts["survived"] += 1
        elif status == "NO_COVERAGE":
            counts["noCoverage"] += 1
        elif status == "TIMED_OUT":
            counts["timedOut"] += 1
        elif status == "NON_VIABLE":
            counts["nonViable"] += 1
        elif status == "MEMORY_ERROR":
            counts["memoryError"] += 1

        if status in ("SURVIVED", "NO_COVERAGE"):
            def txt(tag):
                el = m.find(tag)
                return el.text if el is not None else None

            problems.append({
                "status": status,
                "sourceFile": txt("sourceFile"),
                "mutatedClass": txt("mutatedClass"),
                "mutatedMethod": txt("mutatedMethod"),
                "methodDescription": txt("methodDescription"),
                "lineNumber": int(txt("lineNumber") or 0),
                "mutator": txt("mutator"),
                "description": txt("description"),
                "killingTest": txt("killingTest"),
                "numberOfTestsRun": int(m.attrib.get("numberOfTestsRun", "0") or 0),
                "detected": m.attrib.get("detected") == "true",
            })

    payload = {
        "tool": {"name": "pitest", "reportFile": str(XML_PATH)},
        "counts": counts,
        "problems": problems
    }

    OUT_DIR.mkdir(parents=True, exist_ok=True)
    OUT_PATH.write_text(json.dumps(payload, indent=2), encoding="utf-8")
    print(f"Wrote {OUT_PATH} with {len(problems)} problems")

if __name__ == "__main__":
    main()
