#! /bin/bash


## start
sudo docker compose -f docker-compose-uncert.yml up -d
sudo docker compose logs --follow --tail 10 | multilog s1048576 n10 ./logs/logs &


## stop
# kill -9 $(pidof multilog)