# Tips

## Docker

- When in office: Cable in, VPN off, Wifi off. Otherwise connection-problems between the docker-containers.


## Compiling
- Building wrapper
    cd asyncwrapper
    docker compose up
    docker exec -it maven_builder /bin/bash
    mvn package -DskipTests


## Database

- Accessing db from host-machine
    - psql -h 0.0.0.0 -p 5432 -U postgres (pw: postgres)

- Empty all tables:
    - truncate order_job_refs, orders, complex_outputs, complex_outputs_as_inputs, complex_inputs, complex_inputs_as_values, literal_inputs, bbox_inputs, jobs, processes;


