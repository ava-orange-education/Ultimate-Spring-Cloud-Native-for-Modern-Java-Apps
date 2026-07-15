# CampusFlow Architecture

CampusFlow is a compact Spring Boot monolith for school administration — realistic enough to teach from, small enough to understand in one sitting.

The codebase is organized by **domain modules** (`student`, `schoolclass`, `enrollment`, `attendance`, `notification`). Each module follows the same layering:

```
controller → service → repository → entity
```

All modules share **one PostgreSQL schema**, managed by Flyway in `monolith-baseline/src/main/resources/db/migration/`.

## Domain modules

| Domain | Package | Main data | Business responsibility | Role in the monolith today |
|--------|---------|-----------|----------------------|---------------------------|
| **Students** | `student/` | `students` | Maintain student master data (name, email, status) | Core reference data used by enrollment and attendance |
| **Classes** | `schoolclass/` | `classes` | Define courses, terms, capacity, and scheduling | Core reference data used by enrollment and attendance |
| **Enrollment** | `enrollment/` | `enrollments` | Register students in classes; enforce capacity and eligibility | Workflow that links students and classes |
| **Attendance** | `attendance/` | `attendance_records` | Record daily presence or absence per student and class | Operational workflow that depends on enrollment |
| **Notifications** | `notification/` | `notifications` | Send enrollment confirmations and absence alerts | Side-effect handler invoked by other domains |

### Dependencies between domains

| Domain | Depends on | Why |
|--------|------------|-----|
| Enrollment | Students, Classes, Notifications | Needs student/class data to enroll; sends confirmation on success |
| Attendance | Students, Classes, Enrollment, Notifications | Needs enrollment to validate eligibility; sends alert on absence |
| Notifications | Students, Classes (via method parameters) | Receives student and class details from callers — no direct repository calls to other domains |
| Students | — | No dependencies on other domain modules |
| Classes | — | No dependencies on other domain modules |

## Boundary map

This map shows how the domains interact today. Solid lines are direct code dependencies; dashed lines are data relationships through the shared database.

```
                    ┌─────────────┐     ┌─────────────┐
                    │   Students  │     │   Classes   │
                    │  (master)   │     │  (master)   │
                    └──────┬──────┘     └──────┬──────┘
                           │                   │
                           └────────┬──────────┘
                                    │
                           ┌────────▼────────┐
                           │   Enrollment    │──────► Notifications
                           │   (workflow)    │        (side effect)
                           └────────┬────────┘
                                    │ enrollment check
                           ┌────────▼────────┐
                           │   Attendance    │──────► Notifications
                           │  (operational)  │        (side effect)
                           └─────────────────┘
```

| Interaction | Boundary strength | Notes |
|-------------|-------------------|-------|
| Students ↔ Classes | **Strong** (independent) | Separate master data; no direct code calls between them |
| Enrollment → Students, Classes | **Moderate** | Workflow reads master data through service APIs |
| Attendance → Enrollment | **Weak** | Attendance reaches into `EnrollmentRepository` directly — a typical monolith shortcut |
| Enrollment/Attendance → Notifications | **Weak** | Synchronous in-process calls; notification failure would roll back the caller's transaction |
| All domains → shared database | **No boundary** | Foreign keys cross module lines (`enrollments` references `students` and `classes`) |

## Coupling points

These are the places where a future service split would need careful design.

### Enrollment coordinates multiple domains

When a student enrolls in a class, `EnrollmentService`:

1. Loads the student via `StudentService` and checks active status
2. Loads the class via `SchoolClassService` and checks capacity
3. Persists the enrollment
4. Calls `NotificationService.sendEnrollmentConfirmation()` in the same transaction

Enrollment is a **coordination point** — it owns the enrollment record but depends on student and class data to make decisions.

### Attendance validates through enrollment

When attendance is marked, `AttendanceService`:

1. Loads student and class via their services
2. Checks enrollment via `EnrollmentRepository.existsByStudentIdAndSchoolClassId()` — bypassing `EnrollmentService`
3. Persists the attendance record
4. Calls `NotificationService.sendAbsenceAlert()` when status is `ABSENT`

Attendance does not own enrollment data but **requires** it to enforce the business rule that only enrolled students can be marked present or absent.

### Notifications are invoked synchronously

Both enrollment and attendance call `NotificationService` in the same HTTP request and database transaction. The notification module stores a record and logs the message (standing in for an external email provider).

This coupling means:

- Notification logic runs in the caller's transaction boundary
- A notification failure blocks the enrollment or attendance operation
- Notification has no independent lifecycle today

## Extraction seams

Not every module is equally ready to become a separate service. The decision depends on cohesion, coupling, data ownership, change frequency, and risk.

### Strong candidates for early extraction

| Module | Why extract | Cohesion | Coupling | Data ownership | Risk |
|--------|-------------|----------|----------|----------------|------|
| **Notifications** | Clear single purpose (send alerts); own table with no foreign keys; callers pass all needed data | High — one reason to change: how messages are sent | Low — receives data via method calls, does not query other tables | Owns `notifications` table completely | Low — extraction does not break enrollment or attendance logic if communication is decoupled |
| **Attendance** | Distinct operational workflow with its own table and API | High — attendance rules change independently of enrollment rules | Medium — needs enrollment validation; calls notifications | Owns `attendance_records`; reads enrollment state | Medium — must solve "is student enrolled?" across a boundary |

See `services/notification-service/README.md` and `services/attendance-service/README.md` for extraction guides.

### Better kept together for now

| Module | Why stay | Reasoning |
|--------|----------|-----------|
| **Students** | Master data hub | Referenced by enrollment and attendance; splitting early creates widespread read dependencies |
| **Classes** | Master data hub | Same as students — small, stable, heavily referenced |
| **Enrollment** | Core workflow glue | Sits between master data and operational workflows; owns the relationship that attendance depends on |

Extracting students or classes first would force every other module to make remote calls for basic reference data. Extracting enrollment before attendance would leave attendance without a local enrollment check.

A practical order: **notifications first**, then **attendance**, while students/classes/enrollment remain in the core monolith.

## From monolith to services

CampusFlow is designed as a progression:

| Stage | What it looks like | CampusFlow today |
|-------|-------------------|------------------|
| **Structured monolith** | Domain modules with clear packages, but shared database and cross-module calls | Current state — see `monolith-baseline/src/main/java/com/campusflow/` |
| **Modular monolith** | Stronger module boundaries, explicit APIs between domains, reduced shortcuts (e.g. no direct repository access across modules) | A refactoring step the book discusses before any service split |
| **Extracted services** | Selected modules run independently with their own database and API | Target state for notifications and attendance — see `services/` and `gateway/` |

The book uses this progression to show that good boundaries start inside the monolith, not only at deployment time.

## Event Storming

For a workshop-style walkthrough of CampusFlow processes and how they reveal bounded contexts, see [docs/event-storming.md](event-storming.md).

## Data model

```
students ──┬── enrollments ──┬── classes
           │                 │
           └── attendance_records
notifications (standalone — no foreign keys to other tables)
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

## Target architecture (later chapters)

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

The `services/` and `gateway/` folders contain companion guides for this evolution — not standalone running services yet.
