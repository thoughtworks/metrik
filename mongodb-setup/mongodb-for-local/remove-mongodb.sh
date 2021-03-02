#!/bin/bash
docker-compose -f docker-compose-for-local.yml down
docker stop mongodb
docker rm mongodb
rm -rf mongo-db-data

