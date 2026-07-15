# CampusFlow — Companion Code for *Ultimate Spring Cloud Native for Modern Java Apps*

Welcome! This repository contains **CampusFlow**, the hands-on reference application used throughout the book.

CampusFlow is a small school administration system built with Spring Boot. You start with a realistic monolith and evolve it step by step toward a cloud-native architecture — containerization, Kubernetes, observability, service extraction, and more.

> **Note:** This is educational sample code. It is not production software and not copied from any customer codebase.

## What you will build

| Module | What it does |
|--------|--------------|
| **Students** | Manage student master data |
| **Classes** | Manage courses, terms, and capacity |
| **Enrollment** | Register students in classes |
| **Attendance** | Record daily attendance |
| **Notifications** | Send alerts when students enroll or are absent |

The baseline is intentionally monolithic: one application, one database, and some cross-module coupling — exactly the kind of system teams modernize in real life.

The `services/` and `gateway/` folders are **companion guides** for later chapters. They point to monolith code and show extraction and routing patterns — they are not standalone runnable services.

## Quick start (5 minutes)

### 1. Clone the repository

```bash
git clone https://github.com/ava-orange-education/Ultimate-Spring-Cloud-Native-for-Modern-Java-Apps.git
cd Ultimate-Spring-Cloud-Native-for-Modern-Java-Apps
```

### 2. Start the application (Docker — recommended)

**Prerequisites:** Docker Desktop running.

```bash
docker compose -f docker/docker-compose.yml up --build
```

Wait until the application is ready, then open:

- Health check: http://localhost:8080/actuator/health
- API: http://localhost:8080/api/students

**Stop the application:**

```bash
docker compose -f docker/docker-compose.yml down
```

### 3. Verify it works

Run these commands **one at a time**:

```bash
curl http://localhost:8080/api/students
```

```bash
curl http://localhost:8080/api/classes
```

```bash
curl -X POST http://localhost:8080/api/enrollments \
  -H 'Content-Type: application/json' \
  -d '{"studentId":1,"classId":2}'
```

**Expected:** a JSON response listing three seed students (Alex, Jordan, Sam), two classes (Algebra I, World History), and a successful enrollment.

### Alternative: run without Docker

If you prefer a local PostgreSQL setup:

**Prerequisites:** Java 21, Maven 3.9+, PostgreSQL 16+

```bash
# Create database and user (PostgreSQL)
createdb campusflow
# user/password: campusflow / campusflow

cd monolith-baseline
mvn spring-boot:run
```

## Seed data

Flyway loads sample data on startup (`monolith-baseline/src/main/resources/db/migration/V2__seed_data.sql`):

| Students | Classes |
|----------|---------|
| Alex Morgan (id: 1) | Algebra I (id: 1) |
| Jordan Lee (id: 2) | World History (id: 2) |
| Sam Patel (id: 3) | |

Alex and Jordan are pre-enrolled in Algebra I. Use these IDs in the book's examples.

## Repository map

```
├── monolith-baseline/     ← Start here: the Spring Boot monolith
├── docker/                ← Containerization (Dockerfile, docker-compose)
├── k8s/                   ← Kubernetes manifests
├── services/              ← Service boundary and extraction guides (Ch. 5, 14)
├── gateway/               ← Spring Cloud Gateway routing guide (Ch. 7, 14)
├── docs/                  ← Architecture notes and chapter guides
└── .github/workflows/     ← CI/CD pipeline example
```

## How to follow the book

Each chapter builds on the previous one. Use this table to find the code discussed in the book:

| Book topic | Where to look |
|------------|---------------|
| Monolith structure and domain model | `monolith-baseline/src/main/java/com/campusflow/` |
| REST API and validation | `*/controller/`, `*/dto/` packages |
| Shared database and coupling | `docs/architecture.md` |
| Domain events and boundaries | `enrollment/event/`, `notification/listener/`, `docs/event-storming.md` |
| Externalized configuration | `monolith-baseline/src/main/resources/application.yml` |
| Feature flags | `monolith-baseline/src/main/java/com/campusflow/config/AppProperties.java` |
| Database migrations (Flyway) | `monolith-baseline/src/main/resources/db/migration/` |
| Error handling | `monolith-baseline/src/main/java/com/campusflow/common/exception/GlobalExceptionHandler.java` |
| Actuator, health, metrics | `monolith-baseline/src/main/resources/application.yml` → `management.*` |
| Structured logging | `monolith-baseline/src/main/resources/logback-spring.xml` |
| Dockerfile and local containers | `docker/` |
| Kubernetes deployment | `k8s/` |
| ConfigMaps and Secrets | `k8s/configmap.yaml`, `k8s/secret.example.yaml` |
| Readiness and liveness probes | `k8s/deployment.yaml` |
| CI/CD pipeline | `.github/workflows/ci.yml` |
| Service extraction guides | `services/notification-service/`, `services/attendance-service/` |
| Strangler Fig gateway routing | `gateway/` |

For the chapter-by-chapter guide (Ch. 1–14), see [docs/learning-path.md](docs/learning-path.md).

## API reference

Base URL: `http://localhost:8080`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/students` | List all students |
| `GET` | `/api/students/{id}` | Get one student |
| `POST` | `/api/students` | Create a student |
| `GET` | `/api/classes` | List all classes |
| `POST` | `/api/classes` | Create a class |
| `POST` | `/api/enrollments` | Enroll a student in a class |
| `GET` | `/api/enrollments/by-student/{id}` | Enrollments for a student |
| `GET` | `/api/enrollments/by-class/{id}` | Enrollments for a class |
| `GET` | `/api/attendance?classId=&date=` | Attendance for a class on a date |
| `POST` | `/api/attendance` | Mark attendance |
| `GET` | `/api/notifications` | List sent notifications |
| `GET` | `/actuator/health` | Application health |
| `GET` | `/actuator/metrics` | Application metrics |

### Hands-on exercises

**Create a new student:**

```bash
curl -X POST http://localhost:8080/api/students \
  -H 'Content-Type: application/json' \
  -d '{"firstName":"Taylor","lastName":"Kim","email":"taylor.kim@student.campusflow.example"}'
```

**Mark attendance (absent — triggers a notification):**

```bash
curl -X POST http://localhost:8080/api/attendance \
  -H 'Content-Type: application/json' \
  -d '{"studentId":1,"classId":1,"date":"2026-07-14","status":"ABSENT"}'
```

**Check notifications:**

```bash
curl http://localhost:8080/api/notifications
```

## Build and test

```bash
cd monolith-baseline
mvn verify
```

Tests use an in-memory H2 database and do not require PostgreSQL or Docker.

## Configuration

All settings can be overridden with environment variables:

| Variable | Default | Purpose |
|----------|---------|---------|
| `CAMPUSFLOW_DB_URL` | `jdbc:postgresql://localhost:5432/campusflow` | Database connection |
| `CAMPUSFLOW_DB_USER` | `campusflow` | Database user |
| `CAMPUSFLOW_DB_PASSWORD` | `campusflow` | Database password |
| `SERVER_PORT` | `8080` | HTTP port |
| `CAMPUSFLOW_SCHOOL_NAME` | `CampusFlow Academy` | Display name |
| `CAMPUSFLOW_FEATURE_ATTENDANCE_REMINDERS` | `true` | Send alerts on absence |
| `CAMPUSFLOW_FEATURE_ENROLLMENT_CONFIRMATION` | `true` | Send alerts on enrollment |

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `Couldn't connect to server` on port 8080 | The application is not running. Start it with `docker compose -f docker/docker-compose.yml up --build` or `mvn spring-boot:run`. |
| `Cannot connect to the Docker daemon` | Start Docker Desktop and wait until it is ready. |
| `409 Conflict: Student is already enrolled` | The enrollment already exists. Try a different `studentId`/`classId` pair, e.g. `{"studentId":3,"classId":1}`. |
| `curl: URL rejected: Malformed input` | Run each `curl` command separately. Do not paste API response JSON on the same line as a `curl` command. |
| Maven build fails on database | For tests, just run `mvn verify` (uses H2). For `spring-boot:run`, ensure PostgreSQL is running. |

## Architecture (for curious readers)

The baseline monolith has intentional architectural tensions that later chapters address:

- **Shared database** — all modules use one PostgreSQL schema
- **Cross-domain calls** — enrollment and attendance call the notification service directly
- **Extraction candidates** — notifications and attendance are designed to become separate services

See [docs/architecture.md](docs/architecture.md) for the full picture.

## Prerequisites summary

| Tool | Version | Required for |
|------|---------|--------------|
| Java | 21 | Building and running the monolith |
| Maven | 3.9+ | Build and tests |
| Docker Desktop | latest | Recommended local setup |
| PostgreSQL | 16+ | Only if running without Docker |
| curl | any | API examples in the book |

## License

MIT — see [LICENSE](LICENSE).

---

**Author:** Stefanie Schlüter  
**Publisher:** Orange Education / AVA®
