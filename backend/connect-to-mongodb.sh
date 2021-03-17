#!/usr/bin/env bash

container_id=$(docker ps  --filter="name=mongo" -q)

for file in `find $PWD/src/api-test/resources/ -type f -name '*.js'`
do
  docker exec -i $container_id mongo --port 27017 -u "4km" --authenticationDatabase "4-key-metrics" -p "4000km" 4-key-metrics < $file
done