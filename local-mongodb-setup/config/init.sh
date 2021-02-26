#!/bin/bash
sleep 5
cat /home/mongo/replica-set-init.js | mongo -u admin -p root --quiet
sleep 5
cat /home/mongo/add-user.js | mongo -u admin -p root --quiet