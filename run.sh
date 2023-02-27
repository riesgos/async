#! /bin/bash


## start
sudo docker compose -f docker-compose-uncert.yml up -d 
sudo docker compose -f docker-compose-uncert.yml logs --follow --tail 10 | multilog s1048576 n10 ./logs/logs &


## stop
# kill -9 $(pidof multilog)
# sudo docker compose -f docker-compose-uncert.yml down

## restart
# kill -9 $(pidof multilog)  && sudo docker compose -f docker-compose-uncert.yml down && sudo docker compose -f docker-compose-uncert.yml up -d