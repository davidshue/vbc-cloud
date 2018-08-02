#!/bin/bash

JAVA_OPTS=${JAVA_OPTS:-"-server -Xms64m -Xmx128m -XX:+UseParallelOldGC"}

PROFILE=${VBC_PROFILE:-"default"}

cd /opt
java $JAVA_OPTS -jar -Dspring.profiles.active=$PROFILE $ENVIRONMENT_ARGS license-*.jar