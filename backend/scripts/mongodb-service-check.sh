#!/bin/bash

## This script is for checking if local MongoDB service is ready.
## arg1: Tolerant time (second)

TOLERANCE="$1"

COUNTER=0
SERVICE_STATUS=0

while [ "$SERVICE_STATUS" -eq 0 ] && [ "$COUNTER" -lt "$TOLERANCE" ]; do
  mongo --eval "db.stats()"
  
  if [ $? -eq 0 ]; then
    SERVICE_STATUS=1
    break
  fi
  
  sleep 2
  ((COUNTER+=2))
  echo "MongoDB service is not available... Waiting for retry ($COUNTER seconds so far)"
done

if [ "$SERVICE_STATUS" -eq 1 ]; then
  echo "MongoDB service is ready!"
  exit 0
elif [ "$SERVICE_STATUS" -ne 1 ] || [ "$COUNTER" -ge "$TOLERANCE" ]; then
  echo "MongoDB service is down."
  exit 1
fi
