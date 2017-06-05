#!/bin/bash

PROFILE=${VBC_PROFILE:-"default"}

java -jar -Dspring.profiles.active=$PROFILE /opt/switch-*.jar