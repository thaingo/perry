#!/bin/bash

JAVA_OPT="-Xms128m -Xmx512m"

  if ([ -z "$JM_TARGET" ]); then
    JM_TARGET="api"
    echo "Default value is set: JM_TARGET = $JM_TARGET"
  fi
  if [ "$JM_TARGET" != "api" ] && [ "$JM_TARGET" != "rails" ]; then
    echo "Unknown JM_TARGET: '$JM_TARGET'"
    echo "Possible values: api, rails"
    exit 1
  fi
  
  if ([ -z "$JM_PERRY_PROTOCOL" ]); then
    echo "JM_PERRY_PROTOCOL variable is required"
    exit 1
  fi
  if ([ -z "$JM_PERRY_HOST" ]); then
    echo "JM_PERRY_HOST variable is required"
    exit 1
  fi
  if ([ -z "$JM_PERRY_PORT" ]); then
    echo "JM_PERRY_PORT variable is required"
    exit 1
  fi
  if ([ -z "$JM_USERS_CSV_PATH" ]); then
    echo "$JM_USERS_CSV_PATH variable is required"
    exit 1
  fi

  if ([ -z "$JM_USERS_COUNT" ]); then
    JM_USERS_COUNT="1"
    echo "Default value is set: JM_USERS_COUNT = $JM_USERS_COUNT"
  fi
  if ([ -z "$JM_REQUESTS_PER_USER" ]); then
    JM_REQUESTS_PER_USER="1"
    echo "Default value is set: JM_REQUESTS_PER_USER = $JM_REQUESTS_PER_USER"
  fi
  if ([ -z "$JM_RAMP_UP_PERIOD_SEC" ]); then
    JM_RAMP_UP_PERIOD_SEC="1"
    echo "Default value is set: JM_RAMP_UP_PERIOD_SEC = $JM_RAMP_UP_PERIOD_SEC"
  fi

  echo "Starting performance tests: "
  echo "JM_TARGET = '$JM_TARGET'"
  echo "JM_PERRY_PROTOCOL = '$JM_PERRY_PROTOCOL'"
  echo "JM_PERRY_HOST = '$JM_PERRY_HOST'"
  echo "JM_PERRY_PORT = '$JM_PERRY_PORT'"
  echo "JM_USERS_CSV_PATH = '$JM_USERS_CSV_PATH'"
  echo "JM_USERS_COUNT = '$JM_USERS_COUNT'"
  echo "JM_REQUESTS_PER_USER = '$JM_REQUESTS_PER_USER'"
  echo "JM_RAMP_UP_PERIOD_SEC = '$JM_RAMP_UP_PERIOD_SEC'"

  JMX_DIR="$JMETER_TESTS/$JM_TARGET"
  RESULTS_DIR="$JMX_DIR/results"

  for TEST_FILEPATH in "$JMX_DIR"/*.jmx; do

    TEST_NAME=$(basename "$TEST_FILEPATH" .jmx)

    TEST_RESULTS_DIR="$RESULTS_DIR/$TEST_NAME"
    mkdir "$TEST_RESULTS_DIR"

    $JMETER_HOME/bin/jmeter -n -t $JMX_DIR/$TEST_NAME.jmx  \
      -l $TEST_RESULTS_DIR/$TEST_NAME.jtl  \
      -j $TEST_RESULTS_DIR/$TEST_NAME.log  \
      -e -o $TEST_RESULTS_DIR/$TEST_NAME-report \
      -JJM_TARGET=$JM_TARGET \
      -JJM_PERRY_PROTOCOL=$JM_PERRY_PROTOCOL \
      -JJM_PERRY_HOST=$JM_PERRY_HOST \
      -JJM_PERRY_PORT=$JM_PERRY_PORT \
      -JJM_USERS_CSV_PATH=$JM_USERS_CSV_PATH \
      -JJM_USERS_COUNT=$JM_USERS_COUNT \
      -JJM_REQUESTS_PER_USER=$JM_REQUESTS_PER_USER \
      -JJM_RAMP_UP_PERIOD_SEC=$JM_RAMP_UP_PERIOD_SEC
  done

