# Attendance Service (extraction candidate)

This folder is reserved for extracting the **attendance** bounded context.

## Why attendance is a second candidate

- Reads student and class data but owns attendance records
- Cross-domain validation against enrollments
- Triggers notifications on absence
- Good example for database-per-service vs shared-database discussions

## Planned API surface

```
GET  /api/attendance?classId=&date=
POST /api/attendance
```

## Strangler Fig routing example (future)

```
/api/attendance/**  -> attendance-service
/api/**             -> campusflow-monolith
```

See `gateway/README.md` for the Spring Cloud Gateway chapter scaffold.
