#!/bin/bash

JAVA_OPT="-Xms128m -Xmx512m"

  if ([ -z "$TARGET" ]); then
    TARGET = "api"
    echo "Default value is set: TARGET = $TARGET"
  fi
  if [ "$TARGET" != "api" ] && [ "$TARGET" != "rails" ]; then
    echo "Unknown TARGET: '$TARGET'"
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
    JM_USERS_COUNT = "1"
    echo "Default value is set: JM_USERS_COUNT = $JM_USERS_COUNT"
  fi
  if ([ -z "$JM_REQUESTS_PER_USER" ]); then
    JM_REQUESTS_PER_USER = "1"
    echo "Default value is set: JM_REQUESTS_PER_USER = $JM_REQUESTS_PER_USER"
  fi
  if ([ -z "$JM_RAMP_UP_PERIOD_SEC" ]); then
    JM_RAMP_UP_PERIOD_SEC = "1"
    echo "Default value is set: JM_RAMP_UP_PERIOD_SEC = $JM_RAMP_UP_PERIOD_SEC"
  fi

  echo "Starting performance tests: "
  echo "TARGET = '$TARGET'"
  echo "JM_PERRY_PROTOCOL = '$JM_PERRY_PROTOCOL'"
  echo "JM_PERRY_HOST = '$JM_PERRY_HOST'"
  echo "JM_PERRY_PORT = '$JM_PERRY_PORT'"
  echo "JM_USERS_CSV_PATH = '$JM_USERS_CSV_PATH'"
  echo "JM_USERS_COUNT = '$JM_USERS_COUNT'"
  echo "JM_REQUESTS_PER_USER = '$JM_REQUESTS_PER_USER'"
  echo "JM_RAMP_UP_PERIOD_SEC = '$JM_RAMP_UP_PERIOD_SEC'"

  $JMETER_HOME/bin/jmeter -n -t $JMETER_TESTS/$TARGET/getUsers.jmx -l $JMETER_TESTS/$TARGET/results/resultfile -e -o $JMETER_TESTS/$TARGET/results/web-report \
    -JTARGET=$TARGET \
    -JJM_PERRY_PROTOCOL=$JM_PERRY_PROTOCOL \
    -JJM_PERRY_HOST=$JM_PERRY_HOST \
    -JJM_PERRY_PORT=$JM_PERRY_PORT \
    -JJM_USERS_CSV_PATH=$JM_USERS_CSV_PATH \
    -JJM_USERS_COUNT=$JM_USERS_COUNT \
    -JJM_REQUESTS_PER_USER=$JM_REQUESTS_PER_USER \
    -JJM_RAMP_UP_PERIOD_SEC=$JM_RAMP_UP_PERIOD_SEC

