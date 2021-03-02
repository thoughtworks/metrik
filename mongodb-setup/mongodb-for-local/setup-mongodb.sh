#!/bin/bash

container_name=mongodb

echo "checking if $container_name existance"
status=$(docker inspect --format {{.State.Status}} "$container_name" | head -n 1)
echo "the container $container_name status is: $status"
if [[ ${status} == "running" ]]; then
    echo "the $container_name is already exist"
    exit 0
fi


echo "start $container_name"
docker rm "${container_name}"
#this command to specify the network
export COMPOSE_PROJECT_NAME=4km-container
chmod 400 ../config/keyfile.txt
chmod +x ../config/*.sh
docker-compose -f docker-compose-for-local.yml up  -d

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