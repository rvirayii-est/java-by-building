# syntax=docker/dockerfile:1
#
# A multi-stage Dockerfile for the shop API (lesson 48).
#
# Build it FROM THE ROOT OF THE REPOSITORY (the "context"), because this module's pom.xml has the course's root pom.xml as its parent:
#     docker build -f lessons/48-capstone/solution/Dockerfile -t shop-api:48 .
# In a project of your own that has ONE pom.xml, the context is the project folder and the three COPY lines at the top get simpler.

# ---------- Stage 1: "build" - a full JDK and Maven, used only to make the jar ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
ARG LESSON=lessons/48-capstone/solution

# Copy ONLY the files that decide which libraries are needed, and download them. Docker caches each step ("layer") and reuses it
# while its inputs are unchanged, so this slow step re-runs only when a pom.xml changes, not when you edit a Java file.
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY ${LESSON}/pom.xml ${LESSON}/pom.xml
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw \
 && ./mvnw -B -q -f ${LESSON}/pom.xml dependency:go-offline

# Now the code. (The tests run in CI; the image build only packages.)
COPY ${LESSON}/src ${LESSON}/src
RUN ./mvnw -B -q -f ${LESSON}/pom.xml package -DskipTests \
 && cp ${LESSON}/target/shop-api.jar /workspace/shop-api.jar

# ---------- Stage 2: "runtime" - a small JRE and the jar. Nothing else from stage 1 comes along ----------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Do not run as root: if the app is ever attacked, the attacker is a nobody inside the container.
RUN groupadd --system shop && useradd --system --gid shop --home-dir /app shop \
 && mkdir -p /data && chown shop:shop /data
COPY --from=build /workspace/shop-api.jar /app/shop-api.jar
USER shop

# The database file lives in /data. Mount a volume there or the data disappears with the container.
ENV SHOP_DATA_DIR=/data
VOLUME /data

EXPOSE 8080
# Exec form (a JSON list): java is process 1 and receives the stop signal, so the app can shut down gracefully.
# -XX:MaxRAMPercentage lets the JVM size its memory from the container's limit instead of the whole machine's.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/shop-api.jar"]
