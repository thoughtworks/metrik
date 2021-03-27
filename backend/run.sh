#!/usr/bin/env bash

set -e

JAR=/app/metrik-backend.jar

java ${JAVA_OPTS} -jar -Dspring.profiles.active=$APP_ENV \
                  -Duser.timezone=Asia/Shanghai \
                  "${JAR}"
