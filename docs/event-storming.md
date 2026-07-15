# Event Storming with CampusFlow

Event Storming is a collaborative workshop technique for exploring business processes and discovering domain boundaries. This guide applies it to CampusFlow so you can practice the ideas from Chapter 5 against a concrete system.

## What is Event Storming?

Event Storming brings business and technical stakeholders together around a shared timeline of **domain events** — things that happened in the business, stated in past tense.

| Aspect | Description |
|--------|-------------|
| **Who participates** | Domain experts, developers, product owners, architects |
| **What you do** | Map commands (actions), events (outcomes), actors (who triggers them), and policies (automatic reactions) on a timeline |
| **What you get** | A shared view of the process, visible coupling points, candidate aggregates, and natural bounded context boundaries |
| **Why it helps** | Surfaces hidden dependencies, vocabulary differences between teams, and areas that change for different reasons |

You do not need software running to event-storm CampusFlow. The monolith code and `docs/architecture.md` provide enough domain context.

## CampusFlow example: enrollment to notification

### Actors

| Actor | Role |
|-------|------|
| **Registrar** | Enrolls students in classes |
| **Teacher** | Records daily attendance |
| **System** | Sends notifications automatically |

### Process 1: Student enrollment

```
Registrar
    │
    │  [Command] EnrollStudentInClass
    ▼
Enrollment ──► [Event] StudentEnrolledInClass
    │
    │  [Policy] When StudentEnrolledInClass → send confirmation
    ▼
Notification ──► [Event] EnrollmentConfirmationSent
```

| Element | CampusFlow anchor |
|---------|-------------------|
| Command | `POST /api/enrollments` → `EnrollmentService.enroll()` |
| Event | `StudentEnrolledInClassEvent` published via Spring Application Events |
| Policy | `EnrollmentNotificationListener` reacts when feature flag `enrollment-confirmation` is enabled |
| Code | `enrollment/event/StudentEnrolledInClassEvent.java`, `notification/listener/EnrollmentNotificationListener.java` |
| Aggregate cluster | **Enrollment** — owns the student-class relationship |

### Process 2: Attendance recording

```
Teacher
    │
    │  [Command] MarkAttendance
    ▼
Attendance ──► checks enrollment exists
    │
    │  [Event] AttendanceRecorded
    │
    │  [Policy] When AttendanceRecorded AND status=ABSENT → send alert
    ▼
Notification ──► [Event] AbsenceAlertSent
```

| Element | CampusFlow anchor |
|---------|-------------------|
| Command | `POST /api/attendance` → `AttendanceService.markAttendance()` |
| Event | Row persisted in `attendance_records` table |
| Policy | Feature flag `attendance-reminders` triggers `NotificationService.sendAbsenceAlert()` |
| Aggregate cluster | **Attendance** — owns daily presence records per student and class |

### Process 3: Notification dispatch

```
(any caller)
    │
    │  [Command] SendNotification
    ▼
Notification ──► [Event] NotificationDispatched
```

| Element | CampusFlow anchor |
|---------|-------------------|
| Command | Internal call to `NotificationService.dispatch()` |
| Event | Row persisted in `notifications` table; message logged |
| Aggregate cluster | **Notification** — owns message content, recipient, and delivery status |

## Bounded context candidates

Event Storming often reveals where language and responsibility diverge:

| Context | Ubiquitous language | Owns | Talks to |
|---------|-------------------|------|----------|
| **Student registry** | student, status, email | `students` | Enrollment (read) |
| **Class catalog** | class, term, capacity, teacher | `classes` | Enrollment (read) |
| **Enrollment** | enroll, capacity, eligibility | `enrollments` | Student registry, Class catalog (read); Notifications (notify) |
| **Attendance** | present, absent, attendance date | `attendance_records` | Enrollment (validate); Notifications (notify) |
| **Notifications** | alert, confirmation, recipient | `notifications` | Receives commands from other contexts |

### Where boundaries are clear

- **Notifications** uses its own vocabulary (recipient, subject, type) and owns its data completely. Other contexts do not read the `notifications` table.
- **Students** and **Classes** are stable reference contexts with simple CRUD operations.

### Where boundaries are fuzzy

- **Enrollment** and **Attendance** both use student and class identifiers but mean different things: enrollment establishes a relationship; attendance records a daily fact about that relationship.
- **Attendance** currently reads enrollment data directly via `EnrollmentRepository` — event storming makes this dependency visible and raises the question: should attendance ask enrollment via an API instead?

## How Event Storming helps with microservice boundaries

After mapping CampusFlow processes, three patterns typically emerge:

1. **Natural seams** — Notifications reacts to events from other contexts but owns its own data. It is a strong extraction candidate because the workshop shows it has a distinct lifecycle.

2. **Language conflicts** — "Student" means master data in the registry context but "enrolled student" in the attendance context. Separate services force explicit contracts for which meaning is exchanged.

3. **Change frequency** — Notification delivery channels change often (email, SMS, push). Enrollment rules change less often. Attendance policies may change per school term. Event Storming makes these different rates of change visible and supports splitting along those lines.

Use the results alongside `docs/architecture.md` and the guides in `docs/extraction-guides/` when evaluating whether a boundary is working or needs refinement.
