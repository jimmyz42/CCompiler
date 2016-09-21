#!/bin/bash
: ${1?"Usage: $0 <test-type> <file>"}
: ${2?"Usage: $0 <test-type> <file>"}

TEST_TYPE=$1
STAGE=$2

if [ $TEST_TYPE = "scanner" ]; then
  OUTPUT=tests/$TEST_TYPE/generatedout/$STAGE.out
  echo "" > $OUTPUT
  ./run.sh -t scan tests/$TEST_TYPE/input/$STAGE > >(tee -a $OUTPUT) 2> >(tee -a $OUTPUT >&2)
  tail -n +2 "$OUTPUT" > "$OUTPUT.tmp" && mv "$OUTPUT.tmp" "$OUTPUT"
  diff tests/$TEST_TYPE/output/$STAGE.out $OUTPUT
elif [ $TEST_TYPE = "parser" ]; then
  if [[ $STAGE =~ ^legal-[0-9][0-9] ]]; then
    ./run.sh -t parse tests/$TEST_TYPE/legal/$STAGE 2>&1
    RESULT=`echo $?`
    if [ $RESULT = 0 ]; then
      echo 'Passed'
    else
      echo "Failed"
    fi
  elif [[ $STAGE =~ "^illegal-\d\d" ]]; then
    ./run.sh -t parse tests/"$TEST_TYPE"/illegal/"$STAGE" 2>&1
    RESULT=`echo $?`
    if [ $RESULT = 1 ]; then
      echo 'Passed'
    else
      echo "Failed"
    fi
  fi
fi
