#! /bin/bash

echo "Start checking MongoDB readiness..."
until mongo -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD --eval "print(\"Finally MongoDB is up...\")"
do
  echo "Waiting for MongoDB to come up..."
  sleep 2
done
echo "Stop checking MongoDB readiness..."

echo "Start MongoDB initialization"
echo "Step 1: initializing replica set"
mongo -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD < /app/mongo/mongo-init-replica-set.js
echo "Wait 10 seconds for replica set to run"
sleep 10
echo "Step 2: add user"
mongo -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD < /app/mongo/mongo-create-user.js
echo "MongoDB initialization done"
