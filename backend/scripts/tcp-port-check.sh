#!/bin/bash

## This script is for checking if the specific TCP port is available on HOST.
## arg1: Host / IP
## arg2: Port number you would like to check
## arg3: Tolerant time (second)

HOSTNAME="$1"
PORT="$2"
TOLERANCE="$3"

COUNTER=0
PORT_STATUS=0

while [ "$PORT_STATUS" -ne 0 ] && [ "$COUNTER" -lt "$TOLERANCE" ]; do
  nc -zv "$HOSTNAME" "$PORT" 2>&1 >/dev/null
  
  if [ $? -eq 0 ]; then
    PORT_STATUS=1
    break
  fi
  
  sleep 2
  ((COUNTER+=2))
  echo "[$HOSTNAME:$PORT] is not accessible... Waiting for retry ($COUNTER seconds so far)"
done

if [ "$PORT_STATUS" -eq 1 ]; then
  echo "[$HOSTNAME:$PORT] is up!"
  exit 0
elif [ "$PORT_STATUS" -ne 1 ] || [ "$COUNTER" -ge "$TOLERANCE" ]; then
  echo "[$HOSTNAME:$PORT] is down."
  exit 1
fi