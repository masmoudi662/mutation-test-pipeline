#!/usr/bin/env python3
import os
import json
import sys

"""
Expected input file (JSON):
{
  "testClassName": "IoUtilsGeneratedTest",
  "relativeTestPath": "org/adbcj/mysql/codec/IoUtilsGeneratedTest.java",
  "java": "package org.adbcj.mysql.codec; ... full test class ..."
}
"""

def main():
    if len(sys.argv) != 3:
        print("Usage: inject_test.py <generated_test.json> <module_test_dir>")
        sys.exit(1)

    json_path = sys.argv[1]
    module_test_dir = sys.argv[2]

    if not os.path.isfile(json_path):
        print(f"ERROR: {json_path} not found")
        sys.exit(1)

    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    relative_path = data.get("relativeTestPath")
    java_code = data.get("java")

    if not relative_path or not java_code:
        print("ERROR: JSON missing required fields")
        sys.exit(1)

    # Final absolute path
    output_path = os.path.join(module_test_dir, relative_path)

    # Security: ensure we stay inside test directory
    abs_test_dir = os.path.abspath(module_test_dir)
    abs_output = os.path.abspath(output_path)

    if not abs_output.startswith(abs_test_dir):
        print("ERROR: Attempt to write outside test directory")
        sys.exit(1)

    os.makedirs(os.path.dirname(abs_output), exist_ok=True)

    with open(abs_output, "w", encoding="utf-8") as f:
        f.write(java_code)

    print(f"Injected test at: {abs_output}")

if __name__ == "__main__":
    main()