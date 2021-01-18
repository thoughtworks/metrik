#!/bin/bash

port="$1"
is_health_check_success=0


for i in 1 2 3 4 5
do
  echo "checking status times: $i"
  health_check_url="http://localhost:${port}/actuator/health"
  status=$(curl "${health_check_url}" | head -n 1)
  echo "the container $container_name status is: $status"
   if [[ ${status} == "{\"status\":\"UP\"}" ]]; then
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

