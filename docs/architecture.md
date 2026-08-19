# CampusFlow Architecture

CampusFlow is a compact Spring Boot monolith for school administration. The codebase is organized by domain modules inside one application and one database.

## Application structure

```
com.campusflow
├── config/           # Externalized settings, feature flags, graceful shutdown
├── common/           # Shared exception handling
├── student/          # Student master data
├── schoolclass/      # Classes and terms
├── enrollment/       # Registration workflow and domain events
├── attendance/       # Attendance tracking
└── notification/     # Reacts to events; sends absence alerts
```

Each domain package includes a `package-info.java` that describes its responsibility and role in the monolith.

Each module follows the same layering:

```
controller → service → repository → entity
```

All modules share **one PostgreSQL schema**, managed by Flyway in `monolith-baseline/src/main/resources/db/migration/`.

## Domain modules

| Domain | Package | Main data | Responsibility | Role in monolith |
|--------|---------|-----------|----------------|------------------|
| **Students** | `student/` | `students` | Student master data | Core reference data |
| **Classes** | `schoolclass/` | `classes` | Courses, terms, capacity | Core reference data |
| **Enrollment** | `enrollment/` | `enrollments` | Register students in classes | Coordination workflow; publishes domain events |
| **Attendance** | `attendance/` | `attendance_records` | Daily presence/absence | Operational workflow; validates enrollment |
| **Notifications** | `notification/` | `notifications` | Alerts and confirmations | Reacts to enrollment events; called by attendance |

### Dependencies between domains

| Domain | Depends on | How |
|--------|------------|-----|
| Enrollment | Students, Classes | Service calls to load and validate data |
| Attendance | Students, Classes, Enrollment | Service calls + `EnrollmentVerification` interface (backed locally by `LocalEnrollmentVerification`) |
| Notifications | Enrollment (event) | `EnrollmentNotificationListener` handles `StudentEnrolledInClassEvent` |
| Notifications | Attendance (direct) | `AttendanceService` calls `sendAbsenceAlert()` synchronously |

## Boundary map

```
                    ┌─────────────┐     ┌─────────────┐
                    │   Students  │     │   Classes   │
                    │  (master)   │     │  (master)   │
                    └──────┬──────┘     └──────┬──────┘
                           │                   │
                           └────────┬──────────┘
                                    │
                           ┌────────▼────────┐
                           │   Enrollment    │── publish ──► StudentEnrolledInClassEvent
                           │  (workflow)     │
                           └────────┬────────┘
                                    │ enrollment check
                           ┌────────▼────────┐
                           │   Attendance    │──── direct call ──► NotificationService
                           │  (operational)  │
                           └─────────────────┘

         StudentEnrolledInClassEvent ──► EnrollmentNotificationListener ──► NotificationService
```

| Interaction | Boundary strength | Status |
|-------------|-------------------|--------|
| Students ↔ Classes | Strong (independent) | Implemented |
| Enrollment → Students, Classes | Moderate | Implemented |
| Enrollment → Notification | Weaker (event-based) | **Implemented** — Spring Application Events |
| Attendance → Enrollment | Stable (interface seam) | **Improved** — `EnrollmentVerification` interface; `LocalEnrollmentVerification` adapts locally |
| Attendance → Notification | Weak (direct call) | Implemented — not yet event-based |

## Architectural seam: EnrollmentVerification (implemented)

`AttendanceService` previously called `EnrollmentRepository` directly to verify enrollment. This crossed a module boundary at the persistence layer.

The seam introduces:

- **`EnrollmentVerification`** — an interface in the `enrollment` package that expresses the business question: _is this student enrolled in this class?_
- **`LocalEnrollmentVerification`** — an adapter in the same package that delegates to `EnrollmentRepository`. Behaviour is unchanged.

`AttendanceService` now depends on `EnrollmentVerification` only. A future adapter could call a REST endpoint, consume a replicated read model, or react to enrollment events — with no changes to `AttendanceService`.

**Code:**

- Interface: `enrollment/EnrollmentVerification.java`
- Adapter: `enrollment/LocalEnrollmentVerification.java`
- Consumer: `attendance/service/AttendanceService.java`

## Domain events (implemented)

After a successful enrollment, `EnrollmentService` publishes `StudentEnrolledInClassEvent` via Spring's `ApplicationEventPublisher`.

`EnrollmentNotificationListener` in the notification package reacts to this event and sends a confirmation when the feature flag is enabled.

**Code:**
- Event: `enrollment/event/StudentEnrolledInClassEvent.java`
- Publisher: `enrollment/service/EnrollmentService.java`
- Listener: `notification/listener/EnrollmentNotificationListener.java`

This stays within the monolith — no message broker. It demonstrates how domains can communicate through events instead of direct service calls, which supports Chapters 5 and 6.

Attendance still calls `NotificationService` directly for absence alerts. That coupling is intentional: it shows a realistic mixed state during gradual refactoring.

## Extraction seams

| Module | Why extract / keep | Assessment |
|--------|-------------------|------------|
| **Notifications** | Clear boundary, owns `notifications` table, already event-driven for enrollment | Strong first extraction candidate |
| **Attendance** | Own workflow but depends on enrollment validation | Second candidate; more coordination required |
| **Students, Classes, Enrollment** | Master data and core workflow glue | Keep in core monolith initially |

Companion guides: `docs/extraction-guides/notification-service.md`, `docs/extraction-guides/attendance-service.md`

## From monolith to services

| Stage | CampusFlow |
|-------|------------|
| **Structured monolith** | Current — domain packages, shared DB, mixed coupling (events + direct calls) |
| **Modular monolith** | Book discusses stronger internal boundaries before any service split |
| **Extracted services** | Companion guides in `docs/extraction-guides/` — not implemented as runnable services |

## Event Storming

See [docs/event-storming.md](event-storming.md) for a workshop walkthrough mapped to CampusFlow processes and code.

## Data model

```
students ──┬── enrollments ──┬── classes
           │                 │
           └── attendance_records
notifications (standalone — no foreign keys)
```

## Current deployment (implemented)

```
[ Client ]
    |
    v
[ campusflow-monolith :8080 ]
    |
    v
[ PostgreSQL ]
```

Docker: `docker/` | Kubernetes: `k8s/` | CI: `.github/workflows/ci.yml`

## Target architecture (companion guidance — not implemented)

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

See `docs/extraction-guides/gateway-routing.md` for routing examples.
