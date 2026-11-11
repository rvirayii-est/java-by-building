#!/usr/bin/env bash
# Runs every single-file example under lessons/*/examples. Fails on the first error.
set -euo pipefail
cd "$(dirname "$0")/.."
count=0
for f in lessons/*/examples/*.java; do
  echo "== $f"
  java "$f"
  count=$((count + 1))
done
echo "OK: $count examples ran"
