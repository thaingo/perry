#!/bin/bash

JAVA_OPT="-Xms128m -Xmx512m"

if ([ -z "$TEST_TYPE" ]); then
  TEST_TYPE="smoke"
  echo "Default value is set: TEST_TYPE = smoke"
fi

if [ "$TEST_TYPE" != "smoke" ] && [ "$TEST_TYPE" != "functional" ] && [ "$TEST_TYPE" != "performance" ]; then
  echo "Unknown TEST_TYPE: '$TEST_TYPE'"
  echo "Known types: smoke, functional, performance"
  exit 1
fi


if [ "$TEST_TYPE" == "smoke" ] || [ "$TEST_TYPE" == "functional" ]; then
  if ([ -z "$CANS_API_URL" ]); then
    echo "CANS_API_URL variable is required"
    exit 1
  fi

  if [[ "$TEST_TYPE" == "smoke" ]]; then
    echo "Executing the Smoke Test..."
    TEST_CLASS=gov.ca.cwds.cans.SmokeTestSuite
  elif [[ "$TEST_TYPE" == "functional" ]]; then
    if ([ -z "$PERRY_URL" ]); then
      echo "PERRY_URL variable is required"
      exit 1
    fi
    if ([ -z "$PERRY_LOGIN_FORM_URL" ]); then
      PERRY_LOGIN_FORM_URL="$PERRY_URL/perry/login"
      echo "PERRY_LOGIN_FORM_URL variable is not found. Default value is used: PERRY_LOGIN_FORM_URL/perry/login"
    fi
    echo "Executing the functional Test..."
    TEST_CLASS=gov.ca.cwds.cans.FunctionalTestSuite
  fi

  echo "Starting tests: "
  echo "CANS_API_URL = '$CANS_API_URL'"
  echo "PERRY_URL = '$PERRY_URL'"
  echo "PERRY_LOGIN_FORM_URL = '$PERRY_LOGIN_FORM_URL'"
  echo "TEST_TYPE = '$TEST_TYPE'"
  echo "TEST_CLASS = '$TEST_CLASS'"

  java ${JAVA_OPT} -Dapi.url="${CANS_API_URL}" -Dperry.url="${PERRY_URL}" -Dlogin.form.target.url="${PERRY_LOGIN_FORM_URL}" -cp /opt/cans-api-test/resources:cans-api-test.jar org.junit.runner.JUnitCore ${TEST_CLASS}
fi

if [ "$TEST_TYPE" == "performance" ]; then
  if ([ -z "$JM_TARGET" ]); then
    JM_TARGET="api"
    echo "Default value is set: JM_TARGET = api"
  fi
  if ([ -z "$JM_PERRY_MODE" ]); then
    JM_PERRY_MODE="PROD"
    echo "Default value is set: JM_PERRY_MODE = PROD"
  fi
  if ([ -z "$JM_USERS_CSV_PATH" ]); then
    JM_USERS_CSV_PATH="/opt/cans-api-perf-test/assets/users.csv"
    echo "Default value is set: JM_USERS_CSV_PATH = /opt/cans-api-perf-test/assets/users.csv"
  fi
  if ([ -z "$JM_USERS_COUNT" ]); then
    JM_USERS_COUNT="1"
    echo "Default value is set: JM_USERS_COUNT = 1"
  fi
  if ([ -z "$JM_UPDATE_REQUESTS_PER_USER" ]); then
    JM_UPDATE_REQUESTS_PER_USER="1"
    echo "Default value is set: JM_UPDATE_REQUESTS_PER_USER = 1"
  fi

  if [ "$JM_TARGET" == "api" ]; then
    JM_CANS_API_PATH_ROOT="/"
  elif [ "$JM_TARGET" == "rails" ]; then
    JM_CANS_API_PATH_ROOT="/api/"
  else
    echo "Unknown JM_TARGET: '$JM_TARGET'"
    echo "Possible values: api, rails"
    exit 1
  fi
  if [ "$JM_PERRY_MODE" == "DEV" ] && [ "$JM_PERRY_MODE" == "PROD" ]; then
    echo "Unknown JM_PERRY_MODE: '$JM_PERRY_MODE'"
    echo "Known modes: DEV, PROD"
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
  if ([ -z "$JM_CANS_API_PROTOCOL" ]); then
    echo "JM_CANS_API_PROTOCOL variable is required"
    exit 1
  fi
  if ([ -z "$JM_CANS_API_HOST" ]); then
    echo "JM_CANS_API_HOST variable is required"
    exit 1
  fi
  if ([ -z "$JM_CANS_API_PORT" ]); then
    echo "JM_CANS_API_PORT variable is required"
    exit 1
  fi

  echo "Starting performance tests: "
  echo "JM_TARGET = '$JM_TARGET'"
  echo "JM_PERRY_MODE = '$JM_PERRY_MODE'"
  echo "JM_USERS_CSV_PATH = '$JM_USERS_CSV_PATH'"
  echo "JM_USERS_COUNT = '$JM_USERS_COUNT'"
  echo "JM_UPDATE_REQUESTS_PER_USER = '$JM_UPDATE_REQUESTS_PER_USER'"
  echo "JM_PERRY_PROTOCOL = '$JM_PERRY_PROTOCOL'"
  echo "JM_PERRY_HOST = '$JM_PERRY_HOST'"
  echo "JM_PERRY_PORT = '$JM_PERRY_PORT'"
  echo "JM_CANS_API_PROTOCOL = '$JM_CANS_API_PROTOCOL'"
  echo "JM_CANS_API_HOST = '$JM_CANS_API_HOST'"
  echo "JM_CANS_API_PORT = '$JM_CANS_API_PORT'"

  $JMETER_HOME/bin/jmeter -n -t $JMETER_TESTS/AssessmentsApi.jmx -l $JMETER_TESTS/results/$JM_TARGET/resultfile -e -o $JMETER_TESTS/results/$JM_TARGET/web-report \
    -JJM_TARGET=$JM_TARGET \
    -JJM_PERRY_MODE=$JM_PERRY_MODE \
    -JJM_PERRY_PROTOCOL=$JM_PERRY_PROTOCOL \
    -JJM_PERRY_HOST=$JM_PERRY_HOST \
    -JJM_PERRY_PORT=$JM_PERRY_PORT \
    -JJM_CANS_API_PROTOCOL=$JM_CANS_API_PROTOCOL \
    -JJM_CANS_API_HOST=$JM_CANS_API_HOST \
    -JJM_CANS_API_PORT=$JM_CANS_API_PORT \
    -JJM_CANS_API_PATH_ROOT=$JM_CANS_API_PATH_ROOT \
    -JJM_USERS_CSV_PATH=$JM_USERS_CSV_PATH \
    -JJM_USERS_COUNT=$JM_USERS_COUNT \
    -JJM_UPDATE_REQUESTS_PER_USER=$JM_UPDATE_REQUESTS_PER_USER
fi
