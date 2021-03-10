#! /bin/bash

# Wait until mongo is up to start, and then init the replica set,
# and everything else
echo "Start checking mongod readiness..."
while [[ $(pgrep mongod) == "" ]]
do
  echo "Waiting for mongod to come up..."
  sleep 2
  if [[ $(pgrep mongod) != "" ]]; then
    echo "mongod seems to be started, continuing with the replica set initialization"
  fi
done
echo "Stop checking mongod readiness..."

echo "Start mongodb initialization"
echo "Step 1: initializing replica set"
sleep 20 && mongo -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD < /app/mongo/mongo-init-replica-set.js
echo "Step 2: add user"
sleep 2 && mongo -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD < /app/mongo/mongo-create-user.js
echo "Mongo initilazation done"
