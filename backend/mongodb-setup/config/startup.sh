#!/bin/bash

echo "hellohello"
chmod 400 /app/mongo/keyfile.txt
chown 999 /app/mongo/keyfile.txt
/usr/local/bin/docker-entrypoint.sh mongod -keyFile /app/mongo/keyfile.txt --replSet rs0 --bind_ip_all