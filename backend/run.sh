#!/usr/bin/env bash

set -e

JAR=/app/metrik-backend.jar

java ${JAVA_OPTS} -jar -Dspring.profiles.active=$APP_ENV \
                  -Duser.timezone=Asia/Shanghai \
                  -DDB_USER=$DB_USER \
                  -DDB_PASSWORD=$DB_PASSWORD \
                  -DAES_KEY=$AES_KEY \
                  -DAES_IV=$AES_IV \
                  "${JAR}"
