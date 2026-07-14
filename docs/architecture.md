# CampusFlow Architecture

CampusFlow is a compact Spring Boot monolith for a school administration domain — realistic enough to teach from, small enough to understand in one sitting.

## Application structure

```
com.campusflow
├── config/           # Externalized settings, feature flags, graceful shutdown
├── common/           # Shared exception handling
├── student/          # Student master data
├── schoolclass/      # Classes and terms
├── enrollment/       # Registration workflow
├── attendance/       # Attendance tracking
└── notification/     # Enrollment confirmations and absence alerts
```

Each module uses the same layering:

```
controller → service → repository → entity
```

All modules share **one PostgreSQL schema**, managed by Flyway in `monolith-baseline/src/main/resources/db/migration/`.

## Business domains

| Domain | Main entity | Responsibility |
|--------|-------------|----------------|
| Students | `Student` | Core master data |
| Classes | `SchoolClass` | Scheduling and capacity |
| Enrollment | `Enrollment` | Links students to classes |
| Attendance | `AttendanceRecord` | Daily attendance records |
| Notifications | `Notification` | Enrollment confirmations and absence alerts |

## Patterns the book modernizes

### Shared database

All tables live in one PostgreSQL database. Foreign keys cross module boundaries — for example, `enrollments` references `students` and `classes`.

### Cross-module dependencies

```
EnrollmentService  → StudentService, SchoolClassService, NotificationService
AttendanceService  → StudentService, SchoolClassService, EnrollmentRepository, NotificationService
```

`AttendanceService` validates enrollment through `EnrollmentRepository` directly — a common shortcut in mature monoliths.

### Synchronous notification calls

Enrollment and attendance invoke `NotificationService` in the same request. Later chapters cover asynchronous messaging and service extraction.

### Externalized configuration

Settings in `application.yml` are overridden by environment variables. Kubernetes chapters move these to ConfigMaps and Secrets (`k8s/configmap.yaml`, `k8s/secret.example.yaml`).

## Extraction boundaries

| Module | Monolith code | Companion guide |
|--------|---------------|-----------------|
| Notifications | `monolith-baseline/src/main/java/com/campusflow/notification/` | `services/notification-service/README.md` |
| Attendance | `monolith-baseline/src/main/java/com/campusflow/attendance/` | `services/attendance-service/README.md` |
| API routing | Monolith controllers | `gateway/README.md` |

The `services/` and `gateway/` folders contain guides and examples for later chapters — not standalone running services.

## Data model

```
students ──┬── enrollments ──┬── classes
           │                 │
           └── attendance_records
notifications (standalone table)
```

## Current deployment

```
[ Client ]
    |
    v
[ campusflow-monolith :8080 ]
    |
    v
[ PostgreSQL ]
```

## Target architecture (Ch. 11–14)

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
[ PostgreSQL ] [ own DB ]     [ own DB ]
```

The book uses CampusFlow to compare shared-database migration with database-per-service approaches.
