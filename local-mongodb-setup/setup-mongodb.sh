#!/bin/bash

chmod 400 ./config/keyfile.txt

docker-compose down || true
docker stop mongodb || true
docker rm mongodb || true
docker-compose up -d

is_health_check_success=0
container_name=mongodb

for i in 1 2 3 4 5
do
  echo "checking status times: $i"
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
        echo "initialize replicaSet and add user"
        docker exec $container_name /home/mongo/init.sh
        echo "mongodb set up success ✓✓✓, databaseName=4-key-metrics, username=4km, password=4000km."
    else
        echo "$container_name set up failed XXX"
        exit 1
fi