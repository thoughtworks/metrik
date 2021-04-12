#!/bin/bash
readonly DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export COMPOSE_PROJECT_NAME=4km-docker
docker-compose -f "$DIR"/docker-compose-for-local.yml down
docker stop mongodb
docker rm mongodb
rm -rf "$DIR"/mongo-db-data

