#!/usr/bin/env bash
set -euo pipefail

# Inputs expected as env vars:
#   CLASS_PATH      (e.g., src/main/java/.../Foo.java)
#   TEST_CLASS_PATH (e.g., src/test/java/.../FooTest.java)  # optional but useful
#
# Output:
#   MODULE_DIR, MODULE_POM, TEST_DIR  (written to $GITHUB_ENV if available)

CLASS_PATH="${CLASS_PATH:?CLASS_PATH is required}"

START_DIR="$(dirname "$CLASS_PATH")"
DIR="$START_DIR"

while true; do
  if [[ -f "$DIR/pom.xml" ]]; then
    MODULE_DIR="$DIR"
    break
  fi
  PARENT="$(cd "$DIR/.." && pwd)"
  CURR="$(cd "$DIR" && pwd)"
  if [[ "$PARENT" == "$CURR" ]]; then
    echo "ERROR: Could not find pom.xml above $CLASS_PATH" >&2
    exit 1
  fi
  DIR="$PARENT"
done

MODULE_POM="$MODULE_DIR/pom.xml"

# default test dir: try from TEST_CLASS_PATH, else module standard
TEST_DIR="$MODULE_DIR/src/test/java"
if [[ "${TEST_CLASS_PATH:-}" != "" ]]; then
  TEST_DIR="$(dirname "$TEST_CLASS_PATH")"
fi

echo "MODULE_DIR=$MODULE_DIR"
echo "MODULE_POM=$MODULE_POM"
echo "TEST_DIR=$TEST_DIR"

# If running inside GitHub Actions, export to env
if [[ -n "${GITHUB_ENV:-}" ]]; then
  {
    echo "MODULE_DIR=$MODULE_DIR"
    echo "MODULE_POM=$MODULE_POM"
    echo "TEST_DIR=$TEST_DIR"
  } >> "$GITHUB_ENV"
fi