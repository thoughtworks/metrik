#!/bin/bash
docker-compose -f docker-compose-for-local.yml down
rm -rf mongo-db-data
