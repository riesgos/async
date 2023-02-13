#!/bin/bash

# We make a container specific copy of the jar file, in order
# to avoid concurrent changes in the jar that might conflict
# with other containers that use the very same jar file too.
cp /app.jar /appcopy.jar

# And then we just run the jar.
java -jar /appcopy.jar
