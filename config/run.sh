#!/bin/bash

PROFILE=${VBC_PROFILE:-"default"}
JAVA_OPTS=${JAVA_OPTS:-"-server -Xms64m -Xmx128m -XX:+UseParallelOldGC"}


cd /opt
java $JAVA_OPTS -jar -Dspring.profiles.active=$PROFILE $ENVIRONMENT_ARGS config-*.jar