# Learning Path

This guide maps each chapter of *Ultimate Spring Cloud Native for Modern Java Apps* to folders and files in this repository.

All material is on the `main` branch. Use the table below to find what to open as you read.

## Chapter-by-chapter guide

| Ch. | Chapter title | Open in the repository | What you will find |
|-----|---------------|------------------------|-------------------|
| 1 | Landscape of Modern Software Development | `README.md` | Intro to CampusFlow, repository purpose, and how to follow the book alongside the code |
| 2 | Understanding Monolithic Architectures | `monolith-baseline/`, `docs/architecture.md` | The baseline monolith, shared database, and cross-module coupling |
| 3 | Cloud-Native Migration | `monolith-baseline/`, `docs/architecture.md` | Migration starting point and approaches: lift-and-shift, re-platform, re-architect, Strangler Fig |
| 4 | Principles of Cloud Native Applications | `monolith-baseline/src/main/resources/application.yml`, `config-server/` | Externalized config in the monolith; Config Server as the next step for centralized config |
| 5 | Identifying Microservice Boundaries | `docs/architecture.md`, `docs/event-storming.md`, `docs/extraction-guides/`, `monolith-baseline/.../package-info.java` | Domain modules, boundary map, Event Storming walkthrough, extraction guides |
| 6 | Refactoring Strategies with Spring Boot | `monolith-baseline/src/main/java/com/campusflow/`, `enrollment/event/` | Module layout, domain events, feature flags in `config/AppProperties.java` |
| 7 | Introducing Spring Cloud Components | `config-server/`, `config-repo/`, `docs/extraction-guides/gateway-routing.md` | Minimal Spring Cloud Config Server (native filesystem backend) and Gateway routing guide |
| 8 | Containerizing Java Applications with Docker | `docker/` | Multi-stage `Dockerfile`, `docker-compose.yml` |
| 9 | Orchestrating Cloud-Native Applications with Kubernetes | `k8s/` | Deployment, Service, ConfigMap, Secret example, probes |
| 10 | Observability, Monitoring, and Logging | `monolith-baseline/src/main/resources/application.yml` (`management.*`), `monolith-baseline/src/main/java/com/campusflow/enrollment/metrics/EnrollmentMetrics.java`, `monolith-baseline/src/main/resources/logback-spring.xml`, optional `k8s/servicemonitor.yaml` | Actuator metrics, Prometheus registry, HTTP latency histogram/SLO buckets, custom enrollment counter, structured JSON logging, optional ServiceMonitor |
| 11 | Security in Cloud-Native Environments | `monolith-baseline/src/main/resources/application-oauth2.yml`, `monolith-baseline/src/main/java/com/campusflow/security/`, `monolith-baseline/src/test/java/com/campusflow/security/ResourceServerSecurityTest.java`, `k8s/deployment.yaml`, `k8s/secret.example.yaml` | Optional OAuth2 JWT Resource Server profile, automated security tests, Secret externalization, Pod hardening (`automountServiceAccountToken`, `seccompProfile`) |
| 12 | CI/CD Pipelines for Cloud Native Systems | `.github/workflows/ci.yml`, `monolith-baseline/src/test/java/com/campusflow/CampusFlowPostgresIntegrationTest.java` | PR vs `main` jobs, parallel Maven tests, Testcontainers PostgreSQL, local Docker image tags (no registry push / no deploy) |
| 13 | Operating and Maintaining Cloud-Native Java Applications | `k8s/deployment.yaml`, `README.md` (Troubleshooting) | Health probes, graceful shutdown, local run instructions |
| 14 | Real-World Case Studies and Future of Cloud-Native Java | `docs/architecture.md`, `docs/extraction-guides/` | Target architecture, Strangler Fig routing, extraction case study |

## Migration journey in this repository

| Approach | Book theme | Repository anchor |
|----------|------------|-------------------|
| Lift-and-shift | Run the same application in a container | `docker/` |
| Re-platform | Deploy to Kubernetes | `k8s/` |
| Re-architect | Extract modules into services | `docs/extraction-guides/` |
| Strangler Fig | Route traffic through a gateway | `docs/extraction-guides/gateway-routing.md` |

## What is implemented vs. what the book explains

| Topic | In this repository | Covered primarily in the book |
|-------|-------------------|------------------------------|
| Monolith, REST API, Flyway, feature flags | Yes — `monolith-baseline/` | Ch. 2–6 |
| Domain events (Spring Application Events) | Yes — `StudentEnrolledInClassEvent`, `EnrollmentNotificationListener` | Ch. 5–6 |
| Microservice boundaries, Event Storming | Yes — `docs/architecture.md`, `docs/event-storming.md` | Ch. 5 |
| Service extraction guides | Yes — `docs/extraction-guides/` (companion documentation) | Ch. 5, 14 |
| Spring Cloud Config Server | Yes — `config-server/` + `config-repo/` (minimal native filesystem backend; monolith not wired by default) | Ch. 7 |
| Spring Cloud Gateway routing example | Yes — `docs/extraction-guides/gateway-routing.md` (companion documentation) | Ch. 7 |
| Docker, Compose | Yes — `docker/` | Ch. 8 |
| Kubernetes manifests, probes, ConfigMap/Secret | Yes — `k8s/` | Ch. 9 |
| Actuator metrics, Prometheus registry, custom enrollment counter, structured JSON logging | Yes — monolith config + `EnrollmentMetrics` | Ch. 10 |
| Optional Prometheus Operator ServiceMonitor | Yes — `k8s/servicemonitor.yaml` (requires operator already installed) | Ch. 10 |
| Full monitoring / log aggregation / distributed tracing stacks (Prometheus server, Grafana, ELK, collectors, Jaeger/Tempo) | Not installed by this repository | Ch. 10 (concepts) |
| Optional OAuth2 JWT Resource Server + workload hardening | Yes — profile `oauth2`, `com.campusflow.security`, `k8s/deployment.yaml`, `k8s/secret.example.yaml` | Ch. 11 |
| mTLS / service mesh, external secret operators, admission policies, image-signature enforcement, deployed identity provider | Not installed by this repository | Ch. 11 (concepts) |
| CI pipeline (PR tests, main image build, Testcontainers PostgreSQL) | Yes — `.github/workflows/ci.yml`, `CampusFlowPostgresIntegrationTest` | Ch. 12 |
| Image registries, GitOps, progressive delivery, Jenkins/GitLab CI | Not installed by this repository | Ch. 12 (concepts) |
| Operations, troubleshooting, incident response | Partially — probes, graceful shutdown | Ch. 13 |
| Service mesh, serverless | Not implemented here | Ch. 14 (concepts) |

## Suggested reading order

1. `README.md` — clone, run, and explore the API
2. `monolith-baseline/` and `docs/architecture.md` — understand the baseline system
3. `docs/event-storming.md` — explore domain boundaries (Ch. 5)
4. `config-server/` — try centralized configuration (Ch. 7)
5. `docker/` — containerize and run locally
6. `k8s/` and `.github/workflows/` — deploy and automate
7. `docs/extraction-guides/` — follow the extraction and routing story

## Version tags

This repository does not use milestone Git tags yet. Navigate by folder as you progress through the chapters. Tagged releases may be added later when they reflect verifiable project states.
