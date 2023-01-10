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


