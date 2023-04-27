#!/bin/bash

# This is a helper to make the tests run.
# It is not the command to run all the tests, as this may
# cause some trouble in initializing the pulsar etc correctly.
#
# So there are just a couple of tests for the database interaction
# (but with an embedded H2 for the tests).
docker-compose run mvn mvn test -Dtest=DeusUtilsTest,DatamanagementRepoTest,BboxInputRepoTest
