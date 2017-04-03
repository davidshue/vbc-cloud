#!/bin/bash

PROFILE=${VBC_PROFILE:-"default"}

java -jar -Dspring.profiles.active=$PROFILE /opt/kafka-node-*.jar