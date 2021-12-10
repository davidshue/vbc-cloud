#!/bin/bash

CONFIG_HOST=${CONFIG_HOST:=vbc-config}
CONFIG_PORT=${CONFIG_PORT:=8888}
JAVA_OPTS=${JAVA_OPTS:-"-server -Xms64m -Xmx128m -XX:+UseParallelOldGC"}

a=0;
while ! nc -z ${CONFIG_HOST} ${CONFIG_PORT};
    do
        a=$(($a+1));
        if [ "$a" -gt 20 ]
        then
            echo startup failed!
            exit 0  # startup failed
        fi
        echo sleeping $a;
        sleep 5;
    done;

PROFILE=${VBC_PROFILE:-"default"}

cd /opt
java $JAVA_OPTS -jar -Dspring.profiles.active=$PROFILE $ENVIRONMENT_ARGS openid-*.jar