# Multi-stage build for a compact runtime image (book snippet friendly).
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

COPY monolith-baseline/pom.xml monolith-baseline/pom.xml
COPY monolith-baseline/src monolith-baseline/src

RUN apk add --no-cache maven \
    && mvn -f monolith-baseline/pom.xml -DskipTests package \
    && apk del maven

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S campusflow && adduser -S campusflow -G campusflow
USER campusflow

COPY --from=build /workspace/monolith-baseline/target/campusflow-monolith-*.jar app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
