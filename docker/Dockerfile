# Chapter 8 — multi-stage build for the CampusFlow monolith.
# Build context: repository root.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY monolith-baseline/pom.xml monolith-baseline/pom.xml
RUN mvn -f monolith-baseline/pom.xml dependency:go-offline

COPY monolith-baseline/src monolith-baseline/src
RUN mvn -f monolith-baseline/pom.xml -DskipTests package \
    && set -eu \
    && jars="$(find monolith-baseline/target -maxdepth 1 -type f -name '*.jar' \
         ! -name '*-sources.jar' \
         ! -name '*-javadoc.jar')" \
    && count="$(printf '%s\n' "$jars" | sed '/^$/d' | wc -l | tr -d ' ')" \
    && test "$count" -eq 1 \
    && cp "$jars" /workspace/app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S -g 10001 campusflow \
    && adduser -S -u 10001 -G campusflow campusflow

COPY --from=build --chown=campusflow:campusflow /workspace/app.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

USER campusflow

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
