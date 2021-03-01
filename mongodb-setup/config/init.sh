#!/bin/bash
sleep 15
cat /app/mongo/replica-set-init.js | mongo -u admin -p root --quiet
sleep 2
cat /app/mongo/add-user.js | mongo -u admin -p root --quiet