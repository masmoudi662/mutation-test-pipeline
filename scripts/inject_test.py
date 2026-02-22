#!/usr/bin/env python3
import json, os, sys

def main():
    if len(sys.argv) != 3:
        print("Usage: inject_test.py <generated_test.json> <TEST_DIR>")
        sys.exit(2)

    json_path = sys.argv[1]
    test_dir  = sys.argv[2]

    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    file_name = data["fileName"]
    content   = data["content"]

    os.makedirs(test_dir, exist_ok=True)
    out_path = os.path.join(test_dir, file_name)

    with open(out_path, "w", encoding="utf-8") as f:
        f.write(content)

    print(f"[inject_test] wrote: {out_path}")

if __name__ == "__main__":
    main()