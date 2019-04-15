#!/bin/bash

COGNITO_PROFILES="prod,cognito,liquibase"
MFA_PROFILES="mfa,cognito_refresh"

if [ "$PERRY_MODE" = "COGNITO"  ]; then
  echo "COGNITO MODE"
  PERRY_CONFIG="--spring.config.location=config/perry-prod.yml"
  JAVA_OPTS="-Dspring.profiles.active=$COGNITO_PROFILES"
  if [ "$MFA" = true ] ; then
    echo "LOGIN TYPE: MFA"
    JAVA_OPTS="$JAVA_OPTS,$MFA_PROFILES"
  else
    echo "LOGIN TYPE: OAUTH2"
    JAVA_OPTS="$JAVA_OPTS,oauth2"
  fi
elif [ "$PERRY_MODE" = "CLUSTERED_DEV" ]; then
  echo "CLUSTERED DEV MODE"
  PERRY_CONFIG="--spring.config.location=config/perry-clustered-dev.yml"
  JAVA_OPTS="-Dspring.profiles.active=dev,liquibase"
elif [ "$PERRY_MODE" = "SAF"  ]; then
  echo "PROD MODE"
  PERRY_CONFIG="--spring.config.location=config/perry-prod.yml"
  JAVA_OPTS="-Dspring.profiles.active=prod,saf,liquibase,oauth2"
elif [ "$DEV_MODE" = true ] || [ "$PERRY_MODE" = "DEV" ]; then
  echo "LOCAL DEV MODE"
  PERRY_CONFIG="--spring.config.location=config/perry-dev.yml"
  JAVA_OPTS="-Dspring.profiles.active=dev,liquibase"
else
  echo "COGNITO+MFA MODE BY DEFAULT"
  PERRY_CONFIG="--spring.config.location=config/perry-prod.yml"
  JAVA_OPTS="-Dspring.profiles.active=$COGNITO_PROFILES,$MFA_PROFILES"
fi

if [ "$IDM_MODE" = true ] ; then
    JAVA_OPTS="$JAVA_OPTS,idm"
fi

if [ "$REDIS_ENABLED" = true ] ; then
    JAVA_OPTS="$JAVA_OPTS,redis"
fi

if [ "$IGNORE_OAUTH2_STATE" = true ] ; then
    JAVA_OPTS="$JAVA_OPTS,nostate"
fi

if [ "$SWAGGER" = true ] ; then
    JAVA_OPTS="$JAVA_OPTS,swagger"
fi

#DB2 driver settings
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.queryDataSize=$DB2_QUERY_DATA_SIZE"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.queryCloseImplicit=$DB2_QUERY_CLOSE_IMPLICIT"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.resultSetHoldability=$DB2_RESULT_SET_HOLDABILITY"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.interruptProcessingMode=$DB2_INTERRUPT_PROCESSING_MODE"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.currentPackagePath=$DB2_CURRENT_PACKAGE_PATH"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.statementConcentrator=$DB2_STATEMENT_CONCENTRATOR"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.keepDynamic=$DB2_KEEP_DYNAMIC"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.currentDegree=$DB2_CURRENT_DEGREE"
#DB2 driver trace settings
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.traceDirectory=$DB2_TRACE_DIRECTORY"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.traceFile=$DB2_TRACE_FILE"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.traceLevel=$DB2_TRACE_LEVEL"
JAVA_OPTS="$JAVA_OPTS -Ddb2.jcc.override.traceFileAppend=$DB2_TRACE_FILE_APPEND"

if [ -x /paramfolder/parameters.sh ]; then
    source /paramfolder/parameters.sh
fi

if [ -f /opt/newrelic/newrelic.yml ]; then
    java -javaagent:/opt/newrelic/newrelic.jar  ${JAVA_OPTS} -jar perry.jar server ${PERRY_CONFIG}
else
    java  ${JAVA_OPTS} -jar perry.jar server ${PERRY_CONFIG}
fi
