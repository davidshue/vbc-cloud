#!/bin/bash

PROFILE=${VBC_PROFILE:-"default"}
cd /opt
java -jar -Dspring.profiles.active=$PROFILE config-*.jar