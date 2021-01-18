#!/bin/bash

container_name="$1"
is_health_check_success=0

echo "container name is $container_name"
for i in 1 2 3 4 5
do
  echo "checking status times: $i"
  status=$(docker inspect --format {{.State.Status}} "$container_name" | head -n 1)
  echo "the container $container_name status is: $status"
   if [[ ${status} == "running" ]]; then
          is_health_check_success=1
          break
   fi
   sleep 3
   echo "continue check health for $container_name ${i} times..."
done


if [[ ${is_health_check_success} == 1 ]]; then
        echo "$container_name health check success ✓✓✓"
    else
        echo "$container_name health check failed XXX"
        exit 1
fi

