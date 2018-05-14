#!/bin/bash


if ( ${COGNITO_MODE:?} ) ; then
  echo "COGNITO MODE"
  PERRY_CONFIG="--spring.config.location=config/perry-prod.yml"
  if [ "$SWAGGER" = true ] ; then
    JAVA_OPTS="-Dspring.profiles.active=prod,cognito,liquibase,swagger"
  else
    JAVA_OPTS="-Dspring.profiles.active=prod,cognito,liquibase"
  fi
elif ([ -z "$DEV_MODE" ] || ! $DEV_MODE); then
  echo "PROD MODE"
  PERRY_CONFIG="--spring.config.location=config/perry-prod.yml"
  if [ "$SWAGGER" = true ] ; then
    JAVA_OPTS="-Dspring.profiles.active=prod,saf,liquibase,swagger"
  else
    JAVA_OPTS="-Dspring.profiles.active=prod,saf,liquibase"
  fi
else
  echo "DEV MODE"
  PERRY_CONFIG="--spring.config.location=config/perry-dev.yml"
  JAVA_OPTS="-Dspring.profiles.active=dev,liquibase,swagger"
fi

if [ "$REDIS_ENABLED" = true ] ; then
    JAVA_OPTS="$JAVA_OPTS,redis"
fi

if [ "$IGNORE_OAUTH2_STATE" = true ] ; then
    JAVA_OPTS="$JAVA_OPTS,nostate"
fi

if [ -f /opt/newrelic/newrelic.yml ]; then
    java -javaagent:/opt/newrelic/newrelic.jar  ${JAVA_OPTS} -jar perry.jar server ${PERRY_CONFIG}
else
    java  ${JAVA_OPTS} -jar perry.jar server ${PERRY_CONFIG}
fi
