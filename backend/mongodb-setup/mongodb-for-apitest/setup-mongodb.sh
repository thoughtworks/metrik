#!/bin/bash

container_name=mongodb-for-apitest
export COMPOSE_PROJECT_NAME=mongodb-apitest

echo "removing mongodb-for-apitest if exist"
./remove-mongodb.sh

echo "setting up mongodb-for-apitest"
#this command to specify the network

chmod 400 ../config/keyfile.txt
chmod +x ../config/*.sh
docker-compose -f docker-compose-for-apitest.yml up -d

is_health_check_success=0

for i in 1 2 3 4 5
do
  echo "checking $container_name status times: $i"
  status=$(docker inspect --format {{.State.Status}} "$container_name" | head -n 1)
  echo "the container $container_name status is: $status"
   if [[ ${status} == "running" ]]; then
          is_health_check_success=1
          break
   fi
   sleep 1
   echo "continue check health for $container_name ${i} times..."
done


if [[ ${is_health_check_success} == 1 ]]; then
        echo "initializing replicaSet and add user"
        docker exec $container_name /app/mongo/init.sh
        echo "mongodb set up success ✓✓✓, databaseName=4-key-metrics, username=4km, password=4000km."
    else
        echo "$container_name set up failed XXX"
        exit 1
fi