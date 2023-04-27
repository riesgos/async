#!/bin/bash

# This is a helper to make the tests run.
# This skips one test case that needs more initialization - and fails
# if not all the docker services run.
docker-compose run mvn mvn test -Dtest=\!RiesgosPulsarAsyncWrapperApplicationTests
