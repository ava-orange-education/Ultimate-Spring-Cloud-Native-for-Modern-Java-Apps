# Learning Path

This guide maps each chapter of *Ultimate Spring Cloud Native for Modern Java Apps* to folders and files in this repository.

All material is on the `main` branch. Use the table below to find what to open as you read.

## Chapter-by-chapter guide

| Ch. | Theme | Open in the repository | What you will find |
|-----|-------|------------------------|-------------------|
| 1 | Landscape of Modern Software Development | `README.md` | Intro to CampusFlow, repository purpose, and how to follow the book alongside the code |
| 2 | Understanding Monolithic Architectures | `monolith-baseline/`, `docs/architecture.md` | The baseline monolith, shared database, and cross-module coupling |
| 3 | Cloud-Native Migration | `monolith-baseline/`, `docs/architecture.md` | Migration starting point and approaches: lift-and-shift, re-platform, re-architect, Strangler Fig |
| 4 | Cloud-native principles and Twelve-Factor App | `monolith-baseline/src/main/resources/application.yml` | Externalized config, port binding, backing services, disposability |
| 5 | Bounded contexts and service boundaries | `docs/architecture.md`, `services/` | Domain modules, extraction seams, companion guides |
| 6 | Refactoring and gradual decomposition | `monolith-baseline/src/main/java/com/campusflow/` | Module layout, feature flags in `monolith-baseline/.../config/AppProperties.java` |
| 7 | Contracts, APIs, and stability | `*/controller/`, `*/dto/`, `common/exception/` | REST contracts, validation, consistent error responses |
| 8 | Docker and containerization | `docker/` | Multi-stage `Dockerfile`, `docker-compose.yml` |
| 9 | Kubernetes deployment | `k8s/` | Deployment, Service, ConfigMap, Secret example, probes |
| 10 | Observability: logging and metrics | `logback-spring.xml`, Actuator endpoints | Structured JSON logs, `/actuator/health`, `/actuator/metrics` |
| 11 | Service extraction and communication | `services/notification-service/`, `services/attendance-service/` | Monolith starting points and extraction guides |
| 12 | CI/CD and release automation | `.github/workflows/ci.yml` | Build, test, and container image pipeline |
| 13 | Operations and troubleshooting | `k8s/deployment.yaml`, `README.md` (Troubleshooting) | Health probes, graceful shutdown, local run instructions |
| 14 | Case study, lessons learned, and future directions | `docs/architecture.md`, `gateway/` | Target architecture, Strangler Fig routing example |

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
| Monolith, REST API, Flyway, feature flags | Yes — `monolith-baseline/` | Ch. 2–7 |
| Docker, Compose | Yes — `docker/` | Ch. 8 |
| Kubernetes manifests, probes, ConfigMap/Secret | Yes — `k8s/` | Ch. 9 |
| Actuator health and metrics, structured logging | Yes — monolith config | Ch. 10 |
| Service extraction guides | Yes — `services/` (guides, not full services) | Ch. 11 |
| CI pipeline | Yes — `.github/workflows/ci.yml` | Ch. 12 |
| Distributed tracing, service mesh, serverless | Not implemented here | Ch. 10, 14 (concepts) |
| Security hardening, GitOps, incident response, cost optimization | Not implemented here | Ch. 11–13, 14 (concepts) |

## Suggested reading order

1. `README.md` — clone, run, and explore the API
2. `monolith-baseline/` and `docs/architecture.md` — understand the baseline system
3. `docker/` — containerize and run locally
4. `k8s/` and `.github/workflows/` — deploy and automate
5. `services/` and `gateway/` — follow the extraction and routing story

## Version tags

This repository does not use milestone Git tags yet. Navigate by folder as you progress through the chapters. Tagged releases may be added later when they reflect verifiable project states.
