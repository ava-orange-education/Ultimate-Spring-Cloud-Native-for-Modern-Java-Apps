# Learning Path

This guide maps each chapter of *Ultimate Spring Cloud Native for Modern Java Apps* to folders and files in this repository.

All material is on the `main` branch. Use the table below to find what to open as you read.

## Chapter-by-chapter guide

| Ch. | Chapter title | Open in the repository | What you will find |
|-----|---------------|------------------------|-------------------|
| 1 | Landscape of Modern Software Development | `README.md` | Intro to CampusFlow, repository purpose, and how to follow the book alongside the code |
| 2 | Understanding Monolithic Architectures | `monolith-baseline/`, `docs/architecture.md` | The baseline monolith, shared database, and cross-module coupling |
| 3 | Cloud-Native Migration | `monolith-baseline/`, `docs/architecture.md` | Migration starting point and approaches: lift-and-shift, re-platform, re-architect, Strangler Fig |
| 4 | Principles of Cloud Native Applications | `monolith-baseline/src/main/resources/application.yml` | Externalized config, port binding, backing services, disposability |
| 5 | Identifying Microservice Boundaries | `docs/architecture.md`, `docs/event-storming.md`, `services/`, `monolith-baseline/.../package-info.java` | Domain modules, boundary map, Event Storming walkthrough, extraction guides |
| 6 | Refactoring Strategies with Spring Boot | `monolith-baseline/src/main/java/com/campusflow/`, `enrollment/event/` | Module layout, domain events, feature flags in `config/AppProperties.java` |
| 7 | Introducing Spring Cloud Components | `gateway/README.md` | Spring Cloud Gateway routing example (companion guide, not a runnable service) |
| 8 | Containerizing Java Applications with Docker | `docker/` | Multi-stage `Dockerfile`, `docker-compose.yml` |
| 9 | Orchestrating Cloud-Native Applications with Kubernetes | `k8s/` | Deployment, Service, ConfigMap, Secret example, probes |
| 10 | Observability, Monitoring, and Logging | `monolith-baseline/src/main/resources/logback-spring.xml`, Actuator endpoints | Structured JSON logs, `/actuator/health`, `/actuator/metrics` |
| 11 | Security in Cloud-Native Environments | `k8s/secret.example.yaml` | Secret externalization pattern; broader security practices are covered in the book |
| 12 | CI/CD Pipelines for Cloud Native Systems | `.github/workflows/ci.yml` | Build, test, and container image pipeline |
| 13 | Operating and Maintaining Cloud-Native Java Applications | `k8s/deployment.yaml`, `README.md` (Troubleshooting) | Health probes, graceful shutdown, local run instructions |
| 14 | Real-World Case Studies and Future of Cloud-Native Java | `docs/architecture.md`, `gateway/`, `services/` | Target architecture, Strangler Fig routing, extraction case study |

## Migration journey in this repository

| Approach | Book theme | Repository anchor |
|----------|------------|-------------------|
| Lift-and-shift | Run the same application in a container | `docker/` |
| Re-platform | Deploy to Kubernetes | `k8s/` |
| Re-architect | Extract modules into services | `services/` |
| Strangler Fig | Route traffic through a gateway | `gateway/` |

## What is implemented vs. what the book explains

| Topic | In this repository | Covered primarily in the book |
|-------|-------------------|------------------------------|
| Monolith, REST API, Flyway, feature flags | Yes — `monolith-baseline/` | Ch. 2–6 |
| Domain events (Spring Application Events) | Yes — `StudentEnrolledInClassEvent`, `EnrollmentNotificationListener` | Ch. 5–6 |
| Microservice boundaries, Event Storming | Yes — `docs/architecture.md`, `docs/event-storming.md` | Ch. 5 |
| Service extraction guides | Yes — `services/` (guides, not full services) | Ch. 5, 14 |
| Spring Cloud Gateway routing example | Yes — `gateway/` (guide only) | Ch. 7 |
| Docker, Compose | Yes — `docker/` | Ch. 8 |
| Kubernetes manifests, probes, ConfigMap/Secret | Yes — `k8s/` | Ch. 9 |
| Actuator health and metrics, structured logging | Yes — monolith config | Ch. 10 |
| Security hardening (OAuth2, mTLS, policies) | Not implemented here | Ch. 11 |
| CI pipeline | Yes — `.github/workflows/ci.yml` | Ch. 12 |
| Operations, troubleshooting, incident response | Partially — probes, graceful shutdown | Ch. 13 |
| Distributed tracing, service mesh, serverless | Not implemented here | Ch. 10, 14 (concepts) |

## Suggested reading order

1. `README.md` — clone, run, and explore the API
2. `monolith-baseline/` and `docs/architecture.md` — understand the baseline system
3. `docs/event-storming.md` — explore domain boundaries (Ch. 5)
4. `docker/` — containerize and run locally
5. `k8s/` and `.github/workflows/` — deploy and automate
6. `services/` and `gateway/` — follow the extraction and routing story

## Version tags

This repository does not use milestone Git tags yet. Navigate by folder as you progress through the chapters. Tagged releases may be added later when they reflect verifiable project states.
