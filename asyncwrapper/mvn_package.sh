#!/bin/sh

# Helper script to run the mvn build with the docker container
docker-compose run mvn mvn package -DskipTests=true
