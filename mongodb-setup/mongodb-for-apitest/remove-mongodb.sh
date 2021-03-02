#!/bin/bash
docker-compose -f docker-compose-for-apitest.yml down
docker stop mongodb-for-apitest
docker rm mongodb-for-apitest
rm -rf mongo-db-data
