#!/bin/sh

# Helper script to run the mvn build with the docker container
docker-compose run mvn mvn clean package -DskipTests=true
