#!/bin/bash

## This script is for checking if the backend service is ready.
## arg1: Backend service host / IP
## arg2: Backend service port number
## arg3: Tolerant time (second)

HOSTNAME="$1"
PORT="$2"
TOLERANCE="$3"

HEALTH_CHECK_URL="http://$HOSTNAME:$PORT/actuator/health"

COUNTER=0
SERVICE_STATUS=0

while [ "$SERVICE_STATUS" -eq 0 ] && [ "$COUNTER" -lt "$TOLERANCE" ]; do
  CURL_RESP=$(curl -s "$HEALTH_CHECK_URL" | head -n 1)

  if [[ "$CURL_RESP" == "{\"status\":\"UP\"}" ]]; then
    SERVICE_STATUS=1
    break
  fi
  
  sleep 5
  ((COUNTER+=5))
  echo "Backend service is not ready. Waiting for retry ($COUNTER seconds so far)"
done

if [ "$SERVICE_STATUS" -eq 1 ]; then
  echo "Backend service is ready!"
  exit 0
elif [ "$SERVICE_STATUS" -ne 1 ] || [ "$COUNTER" -ge "$TOLERANCE" ]; then
  echo "Backend service is down."
  exit 1
fi
