#!/bin/bash
docker-compose -f docker-compose-for-apitest.yml down
rm -rf mongo-db-data
