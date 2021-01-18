#!/usr/bin/env bash

set -e

JAR=/app/sea-4-key-metrics-service.jar

java ${JAVA_OPTS} -jar -Dspring.profiles.active=$APP_ENV \
                  -Duser.timezone=Asia/Shanghai \
                  "${JAR}"