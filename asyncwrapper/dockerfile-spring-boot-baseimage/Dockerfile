# Adopted from https://spring.io/guides/gs/spring-boot-docker/
# app.jar will be bound with a mount.
# UPDATE: no longer compiled beforehand.
# Compiles jar automatically now. Only needs to happen once; jar-layer is cached.

FROM maven:3.8.3-openjdk-17

COPY ./ ./

# Installation of dependencies happens separately so it can be cached
RUN mvn install 
RUN mvn package -DskipTests=true
RUN mv ./target/asyncwrapper-0.0.1-SNAPSHOT.jar app.jar


ENTRYPOINT ["java","-jar","/app.jar"]