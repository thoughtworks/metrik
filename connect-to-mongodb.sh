#!/usr/bin/env bash

container_id=$(docker ps  --filter="name=mongo" -q)

docker exec -i $container_id mongo --port 27017 -u "4km" --authenticationDatabase "4-key-metrics" -p "******" < ./feed-test-data.js