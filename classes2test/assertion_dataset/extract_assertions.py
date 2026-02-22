import os
import json
import re

INPUT_FOLDER = r"C:\Users\rider\Desktop\assertion_dataset"   
OUTPUT_FILE = "extracted_assertions.jsonl"
ASSERT_PATTERN = re.compile(
    r"(Assert\.\w+\s*\(.*?\);|assert\w+\s*\(.*?\);)"
)

def extract_assertions(test_body):
    return ASSERT_PATTERN.findall(test_body)

def process_json_file(filepath):
    with open(filepath, "r", encoding="utf-8") as f:
        data = json.load(f)

    test_body = data["test_case"]["body"]
    focal_method_body = data["focal_method"]["body"]
    focal_method_name = data["focal_method"]["identifier"]
    test_method_name = data["test_case"]["identifier"]

    assertions = extract_assertions(test_body)

    records = []
    for assertion in assertions:
        record = {
            "file": os.path.basename(filepath),
            "test_method": test_method_name,
            "focal_method": focal_method_name,
            "assertion": assertion.strip(),
            "test_body": test_body,
            "focal_method_body": focal_method_body
        }
        records.append(record)

    return records


def main():
    all_records = []

    for filename in os.listdir(INPUT_FOLDER):
        if filename.endswith(".json"):
            filepath = os.path.join(INPUT_FOLDER, filename)
            records = process_json_file(filepath)
            all_records.extend(records)

    with open(OUTPUT_FILE, "w", encoding="utf-8") as out:
        for rec in all_records:
            out.write(json.dumps(rec) + "\n")

    print(f"✅ Extracted {len(all_records)} assertions")
    print(f"Saved to {OUTPUT_FILE}")


if __name__ == "__main__":
    main()