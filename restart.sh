#! /bin/bash
sudo docker-compose --file docker-compose-with-wrappers.yml down -v
sudo docker ps -aq | xargs sudo docker stop | xargs sudo docker rm
sudo docker-compose --file docker-compose-with-wrappers.yml up -d --build
sleep 60
sudo docker-compose -f docker-compose-with-wrappers.yml stop riesgos-wps
sudo docker-compose -f docker-compose-with-wrappers.yml start riesgos-wps