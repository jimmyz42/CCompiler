#!/bin/bash
: ${1?"Usage: $0 <test-type> <file>"}
: ${2?"Usage: $0 <test-type> <file>"}

TEST_TYPE=$1
STAGE=$2

OUTPUT=tests/$1/generatedout/$2.out
echo "" > $OUTPUT
./run.sh -t scan tests/$1/input/$2 > >(tee -a $OUTPUT) 2> >(tee -a $OUTPUT >&2)
tail -n +2 "$OUTPUT" > "$OUTPUT.tmp" && mv "$OUTPUT.tmp" "$OUTPUT"
diff tests/$1/output/$2.out $OUTPUT