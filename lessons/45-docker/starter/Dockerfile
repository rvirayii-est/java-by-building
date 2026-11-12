# A first, naive Dockerfile. It would work, but it is big, slow to rebuild and unsafe. Improve it until DockerFilesTest is green:
#     ../../../mvnw test -Dtest=DockerFilesTest
#
# Build from the ROOT of the repository:  docker build -f lessons/45-docker/starter/Dockerfile -t shop-api:45 .

# TODO 1a: make this a MULTI-STAGE build. Name this first stage "build" and pin a version: eclipse-temurin:21-jdk AS build
FROM eclipse-temurin:latest
WORKDIR /workspace
ARG LESSON=lessons/45-docker/starter

# TODO 1b: copy only mvnw, pom.xml, .mvn and ${LESSON}/pom.xml FIRST and run "./mvnw -B -q -f ${LESSON}/pom.xml dependency:go-offline",
# THEN copy ${LESSON}/src and run "package -DskipTests" (so editing a Java file does not download every library again).
COPY . .
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q -f ${LESSON}/pom.xml package -DskipTests

# TODO 2a: start a SECOND stage from eclipse-temurin:21-jre AS runtime, and copy only the jar into it with
#          COPY --from=build /workspace/${LESSON}/target/shop-api.jar /app/shop-api.jar   (use the path /workspace/shop-api.jar after a cp)
# TODO 2b: create a non-root user (groupadd/useradd), a /data folder owned by it, and switch with USER.
# TODO 2c: ENV SHOP_DATA_DIR=/data, VOLUME /data, EXPOSE 8080
# TODO 2d: use the exec form:  ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/shop-api.jar"]
EXPOSE 8080
ENTRYPOINT java -jar /workspace/lessons/45-docker/starter/target/shop-api.jar
