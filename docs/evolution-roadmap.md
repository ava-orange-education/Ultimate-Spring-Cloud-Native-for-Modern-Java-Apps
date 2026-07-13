# Evolution Roadmap

This document maps repository folders to likely book chapters.

## Chapter map

| Chapter theme | Repository location | Key artifacts |
|---------------|---------------------|---------------|
| Legacy monolith anatomy | `monolith-baseline/` | Package structure, shared DB |
| Externalized configuration | `monolith-baseline/src/main/resources/application.yml` | Env vars, profiles |
| Database migrations | `monolith-baseline/src/main/resources/db/migration/` | Flyway scripts |
| Feature flags | `AppProperties`, `application.yml` | `campusflow.features.*` |
| Containerization | `docker/` | Dockerfile, compose |
| Health and metrics | Actuator endpoints | `/actuator/health`, `/actuator/metrics` |
| Structured logging | `logback-spring.xml` | JSON logs |
| Kubernetes basics | `k8s/` | Deployment, Service, probes |
| ConfigMaps and Secrets | `k8s/configmap.yaml`, `k8s/secret.example.yaml` | External config |
| CI/CD | `.github/workflows/ci.yml` | Build, test, image |
| Strangler Fig | `gateway/`, `services/` | Routing, extraction |
| Extract notifications | `services/notification-service/` | First service split |
| Extract attendance | `services/attendance-service/` | Second service split |
| Observability | Actuator + future OpenTelemetry chapter | Metrics, tracing hooks |
| Resilience | Future additions | Timeouts, retries, circuit breakers |

## Suggested Git tags

| Tag | Meaning |
|-----|---------|
| `v0.1-monolith-baseline` | Initial monolith (current state) |
| `v0.2-containerized` | Docker chapter complete |
| `v0.3-k8s-deploy` | Kubernetes chapter complete |
| `v0.4-notification-extracted` | First service extraction |
| `v0.5-gateway-routing` | Gateway and strangler routing |

## Migration patterns demonstrated

1. **Lift-and-shift** — run the same JAR in Docker (`docker/`)
2. **Re-platform** — Kubernetes Deployment with probes (`k8s/`)
3. **Re-architect** — extract services (`services/`)
4. **Strangler Fig** — gateway routes incrementally (`gateway/`)

## What stays in the monolith (for a long time)

- Student and class master data
- Enrollment (coordination across core aggregates)

Extract operational and cross-cutting concerns first (notifications, then attendance).
