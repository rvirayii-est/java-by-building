#!/usr/bin/env bash
# Runs every single-file example under lessons/*/examples. Fails on the first error.
# Lessons 25-32 talk to the H2 database, so the H2 jar is put on the class path for every example.
# The jar comes from your Maven cache (~/.m2). If it is not there yet it is downloaded once with the Maven wrapper.
# To use another copy: H2_JAR=/path/to/h2.jar bash scripts/run-examples.sh
set -euo pipefail
cd "$(dirname "$0")/.."

H2_VERSION=$(sed -n 's:.*<h2.version>\(.*\)</h2.version>.*:\1:p' pom.xml)
H2_JAR="${H2_JAR:-$HOME/.m2/repository/com/h2database/h2/$H2_VERSION/h2-$H2_VERSION.jar}"
if [ ! -f "$H2_JAR" ]; then
  echo "Downloading H2 $H2_VERSION ..."
  ./mvnw -B -q dependency:get -Dartifact="com.h2database:h2:$H2_VERSION"
fi

count=0
for f in lessons/*/examples/*.java; do
  echo "== $f"
  java -cp "$H2_JAR" "$f"
  count=$((count + 1))
done
echo "OK: $count examples ran"
