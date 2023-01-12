# Tips

## Docker

- When in office: Cable in, VPN off, Wifi off. Otherwise connection-problems between the docker-containers.
- It takes a while for first websocket-connections to be established.

## Compiling

- Building wrapper
    cd asyncwrapper
    <!-- docker compose up -d
    docker exec -it riesgos-mvn-builder /bin/bash
    mvn package -DskipTests -->
    ./mvn_package.sh


## Database

- Accessing db from host-machine
    - psql -h 0.0.0.0 -p 5432 -U postgres (pw: postgres)

- Empty all tables:
    - truncate order_job_refs, orders, complex_outputs, complex_outputs_as_inputs, complex_inputs, complex_inputs_as_values, literal_inputs, bbox_inputs, jobs, processes;


## Notes

- Good: I've observed services attempting to do a failed calculation again when a new order comes in.

## Ongoing problems

- Attempt to re-connect to wps from wrapper a few times if network-connection is shaky.


- Minio bucket /riesgosfiles wasn't public for me initially ... even though `entrypoint.sh` does run `/usr/bin/mc anonymous set download minio/${MINIO_BUCKET_NAME}`.
    - Had to set it public in the UI at localhost:9090
    - Might have been a one-time thing. Probably error on very first setup, which has now been persisted in the mount.


- It's possible for wrappers to start a process several times by accident. I just had deus being run thrice; with the following inputs:
    - [ { "id": 100, "product_type_id": 28, "name": "org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess output (100)" }, { "id": 95, "product_type_id": 27, "name": "org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess output (95)" }, { "id": 101, "product_type_id": 26, "name": "org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess output (101)" } ]	106	org.n52.gfz.riesgos.algorithm.impl.DeusProcess output (106)	[]
    - Maybe wrappers don't check if an input-combination is currently being processed.


