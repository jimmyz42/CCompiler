#!/bin/bash
: ${1?"Usage: $0 <test-type>"}

TEST_TYPE=$1

if [ $TEST_TYPE = "scanner" ]; then
  for testfile in char1 char2 char3 char4 char5 char6 char7 char8 char9 hexlit1 \
    hexlit2 hexlit3 id1 id2 id3 number1 number2 number3 op1 op2 string1 string2 \
    string3 tokens1 tokens2 tokens3 tokens4 ws1 ws2;
  do
    OUTPUT=tests/"$TEST_TYPE"/generatedout/"$testfile".out
    echo 'Testing...'$testfile
    ./run.sh -t scan tests/"$TEST_TYPE"/input/"$testfile" &> $OUTPUT
    diff tests/"$TEST_TYPE"/output/"$testfile".out "$OUTPUT"
  done
elif [ $TEST_TYPE = "parser" ]; then
  echo '------------------------------------'
  echo 'Running the legal tests'
  for legalnum in `seq -w 1 25`;
  do
    echo 'Testing...legal-'$legalnum
    OUTPUT=`./run.sh -t parse tests/"$TEST_TYPE"/legal/legal-"$legalnum" 2>&1`
    RESULT=`echo $?`
    if [ $RESULT = 0 ]; then
      echo 'Passed'
    else
      echo "Failed: $OUTPUT"
    fi
  done

  echo '------------------------------------'
  echo 'Running the illegal tests'
  for illegalnum in `seq -w 1 39`;
  do
    echo 'Testing...illegal-'$illegalnum
    OUTPUT=`./run.sh -t parse tests/"$TEST_TYPE"/illegal/illegal-"$illegalnum" 2>&1`
    RESULT=`echo $?`
    if [ $RESULT = 1 ]; then
      echo 'Passed'
    else
      echo "Failed: $OUTPUT"
    fi
  done
fi