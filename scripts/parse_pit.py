#!/usr/bin/env python3
import glob, os, json
import xml.etree.ElementTree as ET

def find_mutations_xml(root="."):
  # Common PIT location: **/target/pit-reports/**/mutations.xml
  patterns = [
    os.path.join(root, "**", "target", "pit-reports", "**", "mutations.xml"),
    os.path.join(root, "**", "pit-reports", "**", "mutations.xml"),
  ]
  hits = []
  for p in patterns:
    hits.extend(glob.glob(p, recursive=True))
  # Prefer newest file if multiple
  hits = [h for h in hits if os.path.isfile(h)]
  hits.sort(key=lambda x: os.path.getmtime(x), reverse=True)
  return hits[0] if hits else None

def parse(xml_path):
  tree = ET.parse(xml_path)
  root = tree.getroot()

  total = 0
  killed = 0
  survived = 0

  # PIT XML: <mutation detected="true/false" status="KILLED|SURVIVED|...">
  for m in root.findall(".//mutation"):
    total += 1
    status = (m.get("status") or "").upper()
    if status == "KILLED":
      killed += 1
    elif status == "SURVIVED":
      survived += 1

  score = (killed / total) if total else 0.0
  return {
    "mutations_xml": xml_path,
    "total_mutations": total,
    "killed_mutations": killed,
    "survived_mutations": survived,
    "mutation_score": score
  }

def main():
  xml_path = find_mutations_xml(".")
  out_dir = os.path.join("artifacts")
  os.makedirs(out_dir, exist_ok=True)

  if not xml_path:
    data = {
      "error": "mutations.xml not found (PIT may have failed or produced no report)",
      "total_mutations": 0,
      "killed_mutations": 0,
      "survived_mutations": 0,
      "mutation_score": 0.0
    }
  else:
    data = parse(xml_path)

  out_path = os.path.join(out_dir, "pit_baseline.json")
  with open(out_path, "w", encoding="utf-8") as f:
    json.dump(data, f, indent=2)
  print(f"Wrote {out_path}")

if __name__ == "__main__":
  main()