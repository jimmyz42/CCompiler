#!/bin/bash
: ${1?"Usage: $0 <test-type>"}

TEST_TYPE=$1


for testfile in char1 char2 char3 char4 char5 char6 char7 char8 char9 hexlit1 \
  hexlit2 hexlit3 id1 id2 id3 number1 number2 number3 op1 op2 string1 string2 \
  string3 tokens1 tokens2 tokens3 tokens4 ws1 ws2;
do
  OUTPUT=tests/"$1"/generatedout/"$testfile".out
  echo 'Testing...'$testfile
  ./run.sh -t scan tests/"$1"/input/"$testfile" &> $OUTPUT
  diff tests/"$1"/output/"$testfile".out "$OUTPUT"
done

