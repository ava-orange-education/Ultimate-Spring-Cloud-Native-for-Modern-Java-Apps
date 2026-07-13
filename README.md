# CampusFlow Reference System

**CampusFlow** is a didactic reference application for a technical book on cloud-native Java with Spring Boot, Spring Cloud, Docker, and Kubernetes.

It is **not** production software and **not** copied from any customer codebase. Everything here is original, educational sample code designed to support architecture explanations, migration examples, and copy-paste-friendly book snippets.

## Domain

CampusFlow models a small school administration platform:

| Module | Responsibility |
|--------|----------------|
| **Students** | Student master data |
| **Classes** | Course and section management |
| **Enrollment** | Student-to-class registration |
| **Attendance** | Daily attendance records |
| **Notifications** | Outbound alerts (email stand-in) |

## Repository layout

```
campusflow-reference/
├── monolith-baseline/     # Working Spring Boot monolith (baseline chapter)
├── docker/                # Dockerfile and docker-compose
├── k8s/                   # Kubernetes manifests
├── services/              # Extraction candidate scaffolds (future chapters)
├── gateway/               # API gateway scaffold (future chapter)
├── docs/                  # Architecture and evolution guides
└── .github/workflows/     # CI pipeline example
```

### Why this structure?

A **single repository with evolution folders** works best for a book:

- Readers clone once and follow a clear path
- Chapters can reference stable paths (`monolith-baseline/...`, `k8s/deployment.yaml`)
- Future stages (`services/`, `gateway/`) can grow without branch switching
- Side-by-side comparison is easy (monolith vs extracted service)

Git tags such as `v0.1-monolith-baseline` can mark chapter milestones later.

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 16 (for local non-Docker runs)
- Docker and Docker Compose (optional, recommended)

## Run locally (PostgreSQL)

1. Start PostgreSQL and create database `campusflow` with user/password `campusflow`.
2. Build and run:

```bash
cd monolith-baseline
mvn spring-boot:run
```

3. Open http://localhost:8080/actuator/health

Environment variables (all optional):

| Variable | Default |
|----------|---------|
| `CAMPUSFLOW_DB_URL` | `jdbc:postgresql://localhost:5432/campusflow` |
| `CAMPUSFLOW_DB_USER` | `campusflow` |
| `CAMPUSFLOW_DB_PASSWORD` | `campusflow` |
| `SERVER_PORT` | `8080` |
| `CAMPUSFLOW_FEATURE_ATTENDANCE_REMINDERS` | `true` |
| `CAMPUSFLOW_FEATURE_ENROLLMENT_CONFIRMATION` | `true` |

## Run with Docker Compose

```bash
docker compose -f docker/docker-compose.yml up --build
```

API: http://localhost:8080  
Health: http://localhost:8080/actuator/health

## Build and test

```bash
cd monolith-baseline
mvn verify
```

Tests use an in-memory H2 database (PostgreSQL mode). CI uses PostgreSQL.

## API overview

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/students` | List students |
| POST | `/api/students` | Create student |
| GET | `/api/classes` | List classes |
| POST | `/api/classes` | Create class |
| POST | `/api/enrollments` | Enroll student in class |
| GET | `/api/attendance?classId=&date=` | Attendance for a class on a date |
| POST | `/api/attendance` | Mark attendance |
| GET | `/api/notifications` | List sent notifications |
| GET | `/actuator/health` | Health check |
| GET | `/actuator/metrics` | Metrics |

Seed data is loaded by Flyway (`V2__seed_data.sql`).

### Example: enroll a student

```bash
curl -X POST http://localhost:8080/api/enrollments \
  -H 'Content-Type: application/json' \
  -d '{"studentId":1,"classId":2}'
```

## Intended evolution (book chapters)

| Stage | Location | Topics |
|-------|----------|--------|
| 1. Baseline monolith | `monolith-baseline/` | Domain model, coupling, shared DB |
| 2. Containerization | `docker/` | Dockerfile, compose, externalized config |
| 3. Observability | `monolith-baseline/` config | Actuator, structured logging, metrics |
| 4. Kubernetes | `k8s/` | Deployments, probes, ConfigMap, Secret |
| 5. Service extraction | `services/` | Strangler Fig, bounded contexts |
| 6. API gateway | `gateway/` | Routing, progressive cutover |
| 7. CI/CD | `.github/workflows/` | Build, test, image pipeline |

See [docs/architecture.md](docs/architecture.md) and [docs/evolution-roadmap.md](docs/evolution-roadmap.md).

## How chapters should reference this repo

- Cite **paths** instead of line numbers when possible (paths stay stable across edits)
- Start from `monolith-baseline` for Java/Spring snippets
- Use `docker/` and `k8s/` for infrastructure chapters
- Use `services/*/README.md` to introduce extraction without spoiling later code

## License

MIT — see [LICENSE](LICENSE).
