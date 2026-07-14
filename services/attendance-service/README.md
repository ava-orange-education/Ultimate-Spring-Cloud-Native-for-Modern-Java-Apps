# Attendance Service

Companion material for the book chapter on extracting the **attendance** module from the monolith.

## Monolith starting point

- `monolith-baseline/src/main/java/com/campusflow/attendance/service/AttendanceService.java`
- `monolith-baseline/src/main/java/com/campusflow/attendance/controller/AttendanceController.java`

## API surface in the monolith

```
GET  /api/attendance?classId=&date=
POST /api/attendance
```

## Try it

```bash
curl "http://localhost:8080/api/attendance?classId=1&date=2026-07-14"

curl -X POST http://localhost:8080/api/attendance \
  -H 'Content-Type: application/json' \
  -d '{"studentId":1,"classId":1,"date":"2026-07-14","status":"PRESENT"}'
```

When attendance is extracted, the API gateway routes `/api/attendance/**` as described in `gateway/README.md`.
