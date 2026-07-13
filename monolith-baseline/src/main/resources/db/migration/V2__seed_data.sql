INSERT INTO students (first_name, last_name, email, status) VALUES
    ('Alex', 'Morgan', 'alex.morgan@student.campusflow.example', 'ACTIVE'),
    ('Jordan', 'Lee', 'jordan.lee@student.campusflow.example', 'ACTIVE'),
    ('Sam', 'Patel', 'sam.patel@student.campusflow.example', 'ACTIVE');

INSERT INTO classes (name, term, room, teacher_name, capacity) VALUES
    ('Algebra I', '2026-Spring', 'B-201', 'Dr. Rivera', 30),
    ('World History', '2026-Spring', 'A-104', 'Ms. Chen', 25);

INSERT INTO enrollments (student_id, class_id, status) VALUES
    (1, 1, 'ACTIVE'),
    (2, 1, 'ACTIVE'),
    (3, 2, 'ACTIVE');
