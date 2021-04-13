#!/bin/bash

## This script is for checking if the specific TCP port is available on HOST.
## arg1: Host / IP
## arg2: Port number you would like to check
## arg3: Tolerant time (second)

HOSTNAME="$1"
PORT="$2"
TOLERANCE="$3"

COUNTER=0

nc -zv "$HOSTNAME" "$PORT" 2>&1 >/dev/null

while [ $? -ne 0 ] && [ "$COUNTER" -lt "$TOLERANCE" ]; do
  sleep 2
  ((COUNTER += 2))
  echo "[$HOSTNAME:$PORT] is down... Waiting for retry (${COUNTER} seconds so far)"
  nc -zv "$HOSTNAME" "$PORT" 2>&1 >/dev/null
done

if [ $? -eq 0 ]; then
  echo "[$HOSTNAME:$PORT] is up!"
  exit 0
elif [ "$COUNTER" -ge "$TOLERANCE" ]; then
  echo "[$HOSTNAME:$PORT] is not accessible, the service is not running."
  exit 1
fi
