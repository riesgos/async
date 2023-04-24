#! /bin/bash
 
set -x           # for debugging only: print out current line before executing it
set -o errexit   # abort on nonzero exitstatus
set -o nounset   # abort on unbound variable
set -o pipefail  # don't hide errors within pipes



COMPOSE_FILE=docker-compose-uncert.yml


read -p "Pull latest state? [y|n] " -n 1 -r
echo    # move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
    VOLUME_FLAGS="-v"
fi

VOLUME_FLAGS=""
read -p "Remove volumes? [y|n] " -n 1 -r
echo    # move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
    VOLUME_FLAGS="-v"
fi

RECOMPILE_WRAPPER=false
read -p "Recompile wrapper? [y|n] " -n 1 -r
echo    # move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
    RECOMPILE_WRAPPER=true
fi

RECOMPILE_FRONTEND=false
read -p "Recompile frontend (required e.g. if server-urls have changed)? [y|n] " -n 1 -r
echo    # move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
    RECOMPILE_FRONTEND=true
fi

# stop all
if ps -p $(pidof multilog) > /dev/null; then
    kill -9 $(pidof multilog)
fi
docker compose -f $COMPOSE_FILE down $VOLUME_FLAGS
rm logs/logs/*

# recompile
if [ "$RECOMPILE_WRAPPER" = true ]; then
    cd asyncwrapper
    docker compose run mvn mvn package -DskipTests=true
    cd ..
fi

## start
docker compose -f $COMPOSE_FILE up -d 
if [ "$RECOMPILE_FRONTEND" = true ]; then
    docker compose -f $COMPOSE_FILE up --build --no-deps frontend -d
fi
docker compose -f $COMPOSE_FILE logs --follow --tail 10 | multilog s1048576 n10 ./logs/logs &
