#!/bin/bash
: ${1?"Usage: $0 <test-type>"}

TEST_TYPE=$1

if [[ $TEST_TYPE =~ scanner.* ]]; then
  for testfile in `ls tests/"$TEST_TYPE"/input`;
  do
    OUTPUT=tests/"$TEST_TYPE"/generatedout/"$testfile".out
    echo 'Testing...'$testfile
    ./runtest.sh "$TEST_TYPE" "$testfile"
  done
elif [[ $TEST_TYPE =~ parser.* ]]; then
  echo '------------------------------------'
  echo 'Running the legal tests'
  for testfile in `ls tests/"$TEST_TYPE"/legal`;
  do
    echo 'Testing...'$testfile
    OUTPUT=`./run.sh -t parse tests/"$TEST_TYPE"/legal/"$testfile" 2>&1`
    RESULT=`echo $?`
    if [ $RESULT = 0 ]; then
      echo 'Passed'
    else
      echo "Failed: $OUTPUT"
    fi
  done

  echo '------------------------------------'
  echo 'Running the hidden illegal tests'
  for testfile in `ls tests/"$TEST_TYPE"/illegal`;
  do
    echo 'Testing...'$testfile
    OUTPUT=`./run.sh -t parse tests/"$TEST_TYPE"/illegal/"$testfile" 2>&1`
    RESULT=`echo $?`
    if [ $RESULT = 1 ]; then
      echo 'Passed'
    else
      echo "Failed: $OUTPUT"
    fi
  done
fi