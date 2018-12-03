#!/bin/bash

if [ "$PERRY_MODE" = "COGNITO"  ]; then
  echo "COGNITO MODE"
  PERRY_CONFIG="--spring.config.location=config/perry-prod.yml"
  JAVA_OPTS="-Dspring.profiles.active=prod,cognito,liquibase"
  if [ "$MFA" = true ] ; then
    echo "LOGIN TYPE: MFA"
    JAVA_OPTS="$JAVA_OPTS,mfa,cognito_refresh"
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
else
  echo "LOCAL DEV MODE"
  PERRY_CONFIG="--spring.config.location=config/perry-dev.yml"
  JAVA_OPTS="-Dspring.profiles.active=dev,liquibase"
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

if [ -x /paramfolder/parameters.sh ]; then
    source /paramfolder/parameters.sh
fi

if [ -f /opt/newrelic/newrelic.yml ]; then
    java -javaagent:/opt/newrelic/newrelic.jar  ${JAVA_OPTS} -jar perry.jar server ${PERRY_CONFIG}
else
    java  ${JAVA_OPTS} -jar perry.jar server ${PERRY_CONFIG}
fi
