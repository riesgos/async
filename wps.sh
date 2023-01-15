#! /bin/bash

sudo docker-compose -f docker-compose-with-wrappers.yml down
sudo docker-compose -f docker-compose-with-wrappers.yml run -d --rm wps-init
sleep 30
sudo docker-compose -f docker-compose-with-wrappers.yml stop riesgos-wps
sudo docker-compose -f docker-compose-with-wrappers.yml start riesgos-wps