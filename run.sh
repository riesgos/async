#! /bin/bash
 
set -x           # for debugging only: print out current line before executing it
set -o errexit   # abort on nonzero exitstatus
set -o nounset   # abort on unbound variable
set -o pipefail  # don't hide errors within pipes



COMPOSE_FILE=docker-compose-uncert.yml

mkdir -p configs

serverImage="gfzriesgos/riesgos-wps"
if [[ -z $( docker image ls | awk '{ print($1) }' | grep $serverImage ) ]]; then
        echo "Building $serverImage ... "
        git clone https://github.com/riesgos/gfz-command-line-tool-repository
        cd gfz-command-line-tool-repository
        docker build -t $serverImage:latest -f assistance/Dockerfile .
        cd .. 
else
        echo "Already exists: $serverImage"
fi


quakeLedgerImage="gfzriesgos/quakeledger"
if [[ -z $( docker image ls | awk '{ print($1) }' | grep $quakeLedgerImage ) ]]; then
        echo "Building $quakeLedgerImage ..."
        git clone https://github.com/gfzriesgos/quakeledger
        cd quakeledger
        docker image build --tag $quakeLedgerImage --file ./metadata/Dockerfile .
        cp metadata/quakeledger.json ../configs
        cd ..
else
        echo "Already exists: $quakeLedgerImage"
fi


assetMasterImage="gfzriesgos/assetmaster"
if [[ -z $( docker image ls | awk '{ print($1) }' | grep $assetMasterImage ) ]]; then
        echo "Building $assetMasterImage ..."
        git clone https://github.com/gfzriesgos/assetmaster
        cd assetmaster
        docker image build --tag $assetMasterImage --file ./metadata/Dockerfile .
        cp metadata/assetmaster.json ../configs
        # @TODO: include data from ...
        cd ..
else
        echo "Already exists: $assetMasterImage"
fi


shakyGroundImage="gfzriesgos/shakyground"
if [[ -z $( docker image ls | awk '{ print($1) }' | grep $shakyGroundImage ) ]]; then
        echo "Building $shakyGroundImage ..."
        git clone https://github.com/gfzriesgos/shakyground
        cd shakyground
        docker image build --tag $shakyGroundImage --file ./metadata/Dockerfile .
        cp metadata/shakyground.json ../configs
        # @TODO: include data ...
        cd ..
else
        echo "Already exists: $shakyGroundImage"
fi


modelPropImage="gfzriesgos/modelprop"
if [[ -z $( docker image ls | awk '{ print($1) }' | grep $modelPropImage ) ]]; then
        echo "Building $modelPropImage ..."
        git clone https://github.com/gfzriesgos/modelprop
        cd modelprop
        docker image build --tag $modelPropImage --file ./metadata/Dockerfile .
        cp metadata/modelprop.json ../configs
        cd ..
else
        echo "Already exists: $modelPropImage"
fi


deusImage="gfzriesgos/deus"
if [[ -z $( docker image ls | awk '{ print($1) }' | grep $deusImage ) ]]; then
        echo "Building $deusImage ..."
        git clone https://github.com/gfzriesgos/deus
        cd deus
        docker image build --tag $deusImage --file ./metadata/Dockerfile .
        cp metadata/deus.json ../configs
        cd ..
else
        echo "Already exists: $deusImage"
fi


tsunamiImage="awi/tssim"
if [[ -z $( docker image ls | awk '{ print($1) }' | grep $tsunamiImage ) ]]; then
        echo "Building $tsunamiImage ..."
        git clone https://gitlab.awi.de/tsunawi/web-services/tsunami-wps
        cd tsunami-wps
        git checkout create-full-docker-build
        # download data from https://nextcloud.awi.de/s/aNXgXxN9qk5RZRz
        # download geoserver- from https://nextcloud.awi.de/....TODO....  (check for sensitive data like passwords!)
        # maybe not required? download `inun` csv files from nextcloud
        docker image build --tag $tsunamiImage --file ./metadata/Dockerfile .
        cp metadata/deus.json ../configs
        cd ..
else
        echo "Already exists: $tsunamiImage"
fi

read -p "Pull latest state? [y|n] " -n 1 -r
echo    # move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
    git pull
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
if [[ ! -z "$(pidof multilog)" ]];  then
    kill -9 $(pidof multilog)
fi
docker compose -f $COMPOSE_FILE down $VOLUME_FLAGS
rm -rf logs/logs/*

# recompile
if [ "$RECOMPILE_WRAPPER" = true ]; then
    cd asyncwrapper
    docker compose run mvn mvn clean package -DskipTests=true
    cd ..
fi

## start
docker compose -f $COMPOSE_FILE up -d 
if [ "$RECOMPILE_FRONTEND" = true ]; then
    docker compose -f $COMPOSE_FILE up --build --no-deps frontend -d
fi
docker compose -f $COMPOSE_FILE logs --follow --tail 10 | multilog s1048576 n10 ./logs/logs &
