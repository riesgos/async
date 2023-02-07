#! /bin/bash

# Removing all containers and volumes (starting with clean db and filestorage)
sudo docker compose --file docker-compose-with-wrappers.yml down -v
# Removing any other containers that might still run
sudo docker ps -aq | xargs sudo docker stop | xargs sudo docker rm
# Restarting with newly built containers (still uses cache, though)
sudo docker compose --file docker-compose-with-wrappers.yml up -d --build
# Leaving some time for containers to boot
sleep 60
# Restarting riesgos-wps
# If not restarted, wps returns references to `localhost:8080` instead of `riesgos-wps:8080`
# Those former references cannot be resolved in the docker-net
# Simply rebooting the wps over the tomcat-admin-ui fails.
sudo docker compose -f docker-compose-with-wrappers.yml stop riesgos-wps
sudo docker compose -f docker-compose-with-wrappers.yml start riesgos-wps