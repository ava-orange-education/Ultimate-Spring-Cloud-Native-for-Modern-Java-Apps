# Book Snippet Index

High-value files for copy-paste into book chapters. Prefer citing the file path so readers can open the full context.

## Spring Boot and configuration

| Topic | File |
|-------|------|
| Application entry point | `monolith-baseline/src/main/java/com/campusflow/CampusFlowApplication.java` |
| Externalized config | `monolith-baseline/src/main/resources/application.yml` |
| Type-safe properties | `monolith-baseline/src/main/java/com/campusflow/config/AppProperties.java` |
| Feature flags | `AppProperties.FeatureFlags` |
| Docker profile | `monolith-baseline/src/main/resources/application-docker.yml` |
| Kubernetes profile | `monolith-baseline/src/main/resources/application-k8s.yml` |
| Graceful shutdown | `monolith-baseline/src/main/java/com/campusflow/config/GracefulShutdownConfig.java` |

## REST API and validation

| Topic | File |
|-------|------|
| REST controller pattern | `monolith-baseline/src/main/java/com/campusflow/student/controller/StudentController.java` |
| Request validation | `monolith-baseline/src/main/java/com/campusflow/student/dto/StudentRequest.java` |
| Global exception handling | `monolith-baseline/src/main/java/com/campusflow/common/exception/GlobalExceptionHandler.java` |

## Domain and coupling (migration chapters)

| Topic | File |
|-------|------|
| Cross-domain enrollment | `monolith-baseline/src/main/java/com/campusflow/enrollment/service/EnrollmentService.java` |
| Attendance + notification coupling | `monolith-baseline/src/main/java/com/campusflow/attendance/service/AttendanceService.java` |
| Notification extraction seam | `monolith-baseline/src/main/java/com/campusflow/notification/service/NotificationService.java` |
| Shared schema | `monolith-baseline/src/main/resources/db/migration/V1__initial_schema.sql` |

## Observability

| Topic | File |
|-------|------|
| Actuator config | `application.yml` (`management.*`) |
| Structured JSON logging | `monolith-baseline/src/main/resources/logback-spring.xml` |
| Health integration test | `monolith-baseline/src/test/java/com/campusflow/enrollment/EnrollmentIntegrationTest.java` |

## Containerization

| Topic | File |
|-------|------|
| Multi-stage Dockerfile | `docker/Dockerfile` |
| Local stack | `docker/docker-compose.yml` |

## Kubernetes

| Topic | File |
|-------|------|
| Deployment + probes | `k8s/deployment.yaml` |
| Service | `k8s/service.yaml` |
| ConfigMap | `k8s/configmap.yaml` |
| Secret template | `k8s/secret.example.yaml` |
| Postgres for demos | `k8s/postgres.yaml` |

## CI/CD

| Topic | File |
|-------|------|
| GitHub Actions pipeline | `.github/workflows/ci.yml` |

## Future chapters (scaffolds)

| Topic | File |
|-------|------|
| Gateway routing plan | `gateway/README.md` |
| Notification extraction plan | `services/notification-service/README.md` |
| Attendance extraction plan | `services/attendance-service/README.md` |
