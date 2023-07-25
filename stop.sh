#! /bin/bash
 
set -x           # for debugging only: print out current line before executing it
set -o errexit   # abort on nonzero exitstatus
set -o nounset   # abort on unbound variable
set -o pipefail  # don't hide errors within pipes



COMPOSE_FILE=docker-compose-uncert.yml


VOLUME_FLAGS=""
read -p "Remove volumes? [y|n] " -n 1 -r
echo    # move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
    VOLUME_FLAGS="-v"
fi


if [[ ! -z "$(pidof multilog)" ]];  then
    kill -9 $(pidof multilog)
fi
docker compose -f $COMPOSE_FILE down $VOLUME_FLAGS
rm -rf logs/logs/*