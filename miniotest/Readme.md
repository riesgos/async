# Test for minio

## Adjust the parameters in the aplication.yml

Set user & password as needed.

## Compile it using maven

You can either run mvn directly:

```bash
mvn clean package -DskipTests=true
```

Or you can run the it via the docker-compose-build:

```bash
docker-compose -f docker-compose-build.yml up -d
docker exec -ti riesgos-mvn-builder mvn clean package -DskipTests=true
docker-compose -f docker-compose-build down
```

## Run it

You can either run it directly via:

```java
java -jar target/miniotest-0.0.1-SNAPSHOT.jar
```

Or you can run it via docker-compose:

```bash
docker-compose -f docker-compose-run.yml up
```

## Upload a file

Go to `http://localhost:8080/upload/test`
to upload a generated file to the s3 bucket.

## Download a file

After you uploaded the file, you can also visit
`http://localhost:8080/upload/test-content` in order
to display the content of the uploaded file.
