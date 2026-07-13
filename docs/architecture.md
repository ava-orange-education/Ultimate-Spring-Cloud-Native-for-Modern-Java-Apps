# CampusFlow Architecture

CampusFlow is a compact Spring Boot monolith that models a plausible internal school administration application — good enough to be realistic, small enough to teach from.

## Current structure (monolith baseline)

```
com.campusflow
├── config/           # Externalized settings, feature flags, graceful shutdown
├── common/           # Shared exception handling
├── student/          # Student master data
├── schoolclass/      # Classes and terms
├── enrollment/       # Registration workflow
├── attendance/       # Attendance tracking
└── notification/     # Outbound messaging (in-process today)
```

Each business area follows the same layering:

```
controller → service → repository → entity
```

All modules share **one PostgreSQL schema** managed by Flyway.

## Bounded contexts (conceptual)

| Context | Aggregates | Notes |
|---------|------------|-------|
| Student Management | Student | Core master data |
| Class Management | SchoolClass | Scheduling and capacity |
| Enrollment | Enrollment | Links students to classes |
| Attendance | AttendanceRecord | Operational daily data |
| Notifications | Notification | Cross-cutting delivery |

These are **logical** boundaries today. Physically, everything runs in one deployable JAR with one database.

## Architectural tensions (intentional, realistic)

### 1. Shared database schema

All entities live in the same PostgreSQL database. Foreign keys cross context boundaries (`enrollments` references `students` and `classes`). This is typical of mature monoliths and motivates later discussions about:

- shared database anti-pattern in microservices
- strangler migrations with dual writes
- read models and eventual consistency

### 2. Cross-domain service dependencies

```
EnrollmentService  → StudentService, SchoolClassService, NotificationService
AttendanceService  → StudentService, SchoolClassService, EnrollmentRepository, NotificationService
```

`AttendanceService` validates enrollment through `EnrollmentRepository` directly — a subtle coupling that mirrors real codebases where teams shortcut through data access layers.

### 3. Synchronous notification calls

Enrollment and attendance trigger notifications in the same transaction path. This is a natural seam for:

- async messaging
- extracting the notification service
- resilience patterns (timeouts, circuit breakers)

### 4. Centralized configuration

`application.yml` plus environment variables control feature flags and integration settings. Later chapters can move these to ConfigMaps, Spring Cloud Config, or external secret stores.

## Extraction candidates

### Primary: Notification service

**Why:** narrow responsibility, own table, called from multiple domains, no complex queries across foreign keys for writes.

**Seam:** replace `NotificationService` method calls with HTTP or message publishing.

### Secondary: Attendance service

**Why:** distinct operational workflow, clear API (`GET/POST /api/attendance`), still needs student/class/enrollment data.

**Seam:** gateway routes `/api/attendance/**` to the new service; monolith keeps authoritative enrollment data initially.

### Gateway candidate

A Spring Cloud Gateway can sit in front of the monolith and progressively route extracted paths. See `gateway/README.md`.

## Why this is a good migration teaching example

| Property | Teaching value |
|----------|----------------|
| Small but non-trivial domain | Enough coupling to matter, not overwhelming |
| Familiar CRUD + workflow | Readers focus on architecture, not exotic logic |
| Clear bounded contexts | Supports DDD and Strangler Fig narratives |
| Actuator-ready | Health, metrics, probes without extra setup |
| Flyway migrations | Schema evolution and deployment ordering |
| Feature flags | Safe progressive rollout stories |
| Docker + K8s manifests | Lift-and-shift and cloud-native deployment |

## Data model (simplified)

```
students ──┬── enrollments ──┬── classes
           │                 │
           └── attendance_records
notifications (standalone table, logical outbound boundary)
```

## Deployment view (baseline)

```
[ Client ]
    |
    v
[ campusflow-monolith :8080 ]
    |
    v
[ PostgreSQL ]
```

## Target view (later chapters)

```
[ Client ]
    |
    v
[ API Gateway ]
    |        |              |
    v        v              v
[ Monolith ] [ Attendance ] [ Notifications ]
    |            |                |
    v            v                v
[ PostgreSQL ] [ DB? ]         [ DB? ]
```

The book can compare shared-database extraction vs database-per-service using the same codebase.
