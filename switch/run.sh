#!/bin/bash

GATEWAY_HOST=${GATEWAY_HOST:=vbc-gateway}
GATEWAY_PORT=${GATEWAY_PORT:=8005}
JAVA_OPTS=${JAVA_OPTS:-"-server -Xms64m -Xmx128m -XX:+UseParallelOldGC"}

a=0;
while ! nc -z ${GATEWAY_HOST} ${GATEWAY_PORT};
    do
        a=$(($a+1));
        if [ "$a" -gt 30 ]
        then
            echo startup failed!
            exit 0  # startup failed
        fi
        echo sleeping $a;
        sleep 5;
    done;

PROFILE=${VBC_PROFILE:-"default"}

sleep 30;

cd /opt
java $JAVA_OPTS -jar -Dspring.profiles.active=$PROFILE $ENVIRONMENT_ARGS switch-*.jar