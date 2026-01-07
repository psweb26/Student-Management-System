-- data.sql: only INSERTs (no TRUNCATE/DROP). Executed after schema.sql.
-- Contents:
--  - Admin (ADM1)
--  - Parents (P1..P8)
--  - Students/Children (RA1..RA8)
--  - Courses (course_code kept same)
--  - Enrollments for each RA*
--  - Fees for each RA*
--  - parent_children mapping P1->RA1, ..., P8->RA8

-- 1. INSERT ADMIN
INSERT INTO students (
    student_id, first_name, last_name, major, grade, email, password,
    phone_number, date_of_birth, address, program, year, advisor
) VALUES
    ('ADM1', 'Admin', 'User', 'Administration', 0, 'admin@uni.com', '1234',
     '+91 98765 00001', '1990-01-01', NULL, 'Admin Portal', NULL, 'System');

-- 2. INSERT PARENT ACCOUNTS (P1..P8)
INSERT INTO students (
    student_id, first_name, last_name, major, grade, email, password,
    phone_number, date_of_birth, address, program, year, advisor
) VALUES
      ('P1', 'P1', 'Parent', 'Parent', 0, 'p1@uni.com', '1234', '+91 70000 00001', '1975-01-01', NULL, NULL, NULL, NULL),
      ('P2', 'P2', 'Parent', 'Parent', 0, 'p2@uni.com', '1234', '+91 70000 00002', '1976-02-02', NULL, NULL, NULL, NULL),
      ('P3', 'P3', 'Parent', 'Parent', 0, 'p3@uni.com', '1234', '+91 70000 00003', '1977-03-03', NULL, NULL, NULL, NULL),
      ('P4', 'P4', 'Parent', 'Parent', 0, 'p4@uni.com', '1234', '+91 70000 00004', '1978-04-04', NULL, NULL, NULL, NULL),
      ('P5', 'P5', 'Parent', 'Parent', 0, 'p5@uni.com', '1234', '+91 70000 00005', '1979-05-05', NULL, NULL, NULL, NULL),
      ('P6', 'P6', 'Parent', 'Parent', 0, 'p6@uni.com', '1234', '+91 70000 00006', '1980-06-06', NULL, NULL, NULL, NULL),
      ('P7', 'P7', 'Parent', 'Parent', 0, 'p7@uni.com', '1234', '+91 70000 00007', '1981-07-07', NULL, NULL, NULL, NULL),
      ('P8', 'P8', 'Parent', 'Parent', 0, 'p8@uni.com', '1234', '+91 70000 00008', '1982-08-08', NULL, NULL, NULL, NULL);

-- 3. INSERT STUDENT USERS (RA1..RA8)
INSERT INTO students (
    student_id, first_name, last_name, major, grade, email, password,
    phone_number, date_of_birth, address, program, year, advisor
) VALUES
      ('RA1', 'Pransh', 'Sharma', 'Computer Science', 10, 'pransh@uni.com', '1234',
       '+91 98765 43210', '2005-01-01', 'Delhi', 'B.Tech CSE', 3, 'Dr. Mehta'),
      ('RA2', 'Aarav', 'Mehta', 'Computer Science & Eng.', 10, 'aarav@uni.com', '1234',
       '+91 98765 43211', '2005-01-01', 'Mumbai', 'B.Tech CSE', 3, 'Dr. Mehta'),
      ('RA3', 'Aisha', 'Gupta', 'Computer Science', 9, 'aisha@uni.com', '1234',
       '+91 98765 43212', '2005-03-15', 'Lucknow', 'B.Tech CSE', 2, 'Dr. Kapoor'),
      ('RA4', 'Rohit', 'Kumar', 'Electrical Engineering', 8, 'rohit@uni.com', '1234',
       '+91 98765 43213', '2005-06-20', 'Jaipur', 'B.Tech EEE', 3, 'Dr. Rao'),
      ('RA5', 'Neha', 'Reddy', 'Mechanical Engineering', 9, 'neha@uni.com', '1234',
       '+91 98765 43214', '2005-08-10', 'Hyderabad', 'B.Tech ME', 3, 'Dr. Patel'),
      ('RA6', 'Karan', 'Singh', 'Civil Engineering', 7, 'karan@uni.com', '1234',
       '+91 98765 43215', '2005-11-25', 'Bhopal', 'B.Tech Civil', 2, 'Dr. Menon'),
      ('RA7', 'Ananya', 'Patel', 'Information Technology', 8, 'ananya@uni.com', '1234',
       '+91 98765 43216', '2005-04-12', 'Ahmedabad', 'B.Tech IT', 3, 'Dr. Mehta'),
      ('RA8', 'Vijay', 'Kumar', 'Computer Science', 9, 'vijay@uni.com', '1234',
       '+91 98765 43217', '2005-02-20', 'Chennai', 'B.Tech CSE', 3, 'Dr. Kapoor');

-- 4. INSERT COURSES (course_code values kept so enrollments remain consistent)
INSERT INTO courses (course_name, course_code, credits) VALUES
('Data Structures & Algorithms', '21CSC203P', 4),
('Transforms and Boundary Value Problems', '20MAT304', 3),
('Professional Ethics', '19ENG101L', 2),
('Computer Organization and Architecture', '21EEE101', 4),
('UHV-I: Universal Human Values', '19ENV101', 2),
('Database Management Systems', '21CSC301', 4),
('Advanced Programming Practice', '21CSC302', 4),
('Operating Systems', '21CSC303', 3),
('Computer Networks', '21CSC304', 3),
('Web Development', '21CSC305', 3);

-- 5. INSERT ENROLLMENTS (use course_id lookups)
-- RA1: full set
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
('RA1', (SELECT course_id FROM courses WHERE course_code = '21CSC203P'), CURDATE(), 'A+'),
('RA1', (SELECT course_id FROM courses WHERE course_code = '20MAT304'), CURDATE(), 'A'),
('RA1', (SELECT course_id FROM courses WHERE course_code = '19ENG101L'), CURDATE(), 'B+'),
('RA1', (SELECT course_id FROM courses WHERE course_code = '21EEE101'), CURDATE(), 'B'),
('RA1', (SELECT course_id FROM courses WHERE course_code = '19ENV101'), CURDATE(), 'C');

-- RA2
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
('RA2', (SELECT course_id FROM courses WHERE course_code = '21CSC203P'), CURDATE(), 'A+'),
('RA2', (SELECT course_id FROM courses WHERE course_code = '20MAT304'), CURDATE(), 'A'),
('RA2', (SELECT course_id FROM courses WHERE course_code = '21CSC301'), CURDATE(), 'A+');

-- RA3
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
('RA3', (SELECT course_id FROM courses WHERE course_code = '21CSC203P'), CURDATE(), 'A'),
('RA3', (SELECT course_id FROM courses WHERE course_code = '21CSC301'), CURDATE(), 'A+'),
('RA3', (SELECT course_id FROM courses WHERE course_code = '21CSC302'), CURDATE(), 'A');

-- RA4
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
('RA4', (SELECT course_id FROM courses WHERE course_code = '21EEE101'), CURDATE(), 'B+'),
('RA4', (SELECT course_id FROM courses WHERE course_code = '20MAT304'), CURDATE(), 'B'),
('RA4', (SELECT course_id FROM courses WHERE course_code = '19ENV101'), CURDATE(), 'A');

-- RA5
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
('RA5', (SELECT course_id FROM courses WHERE course_code = '20MAT304'), CURDATE(), 'A+'),
('RA5', (SELECT course_id FROM courses WHERE course_code = '19ENG101L'), CURDATE(), 'A'),
('RA5', (SELECT course_id FROM courses WHERE course_code = '19ENV101'), CURDATE(), 'B+');

-- RA6
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
('RA6', (SELECT course_id FROM courses WHERE course_code = '20MAT304'), CURDATE(), 'B'),
('RA6', (SELECT course_id FROM courses WHERE course_code = '19ENG101L'), CURDATE(), 'C'),
('RA6', (SELECT course_id FROM courses WHERE course_code = '19ENV101'), CURDATE(), 'B+');

-- RA7
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
('RA7', (SELECT course_id FROM courses WHERE course_code = '21CSC203P'), CURDATE(), 'A'),
('RA7', (SELECT course_id FROM courses WHERE course_code = '21CSC301'), CURDATE(), 'A+'),
('RA7', (SELECT course_id FROM courses WHERE course_code = '21CSC302'), CURDATE(), 'A');

-- RA8
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
('RA8', (SELECT course_id FROM courses WHERE course_code = '21CSC203P'), CURDATE(), 'B+'),
('RA8', (SELECT course_id FROM courses WHERE course_code = '20MAT304'), CURDATE(), 'A'),
('RA8', (SELECT course_id FROM courses WHERE course_code = '19ENG101L'), CURDATE(), 'B');

-- 6. INSERT FEE RECORDS (student IDs RA1..RA8)
INSERT INTO fees (student_id, amount, due_date, status) VALUES
('RA1', 1500.00, '2025-10-30', 'Pending'),
('RA1', 2000.00, '2025-09-15', 'Paid'),
('RA1', 1800.00, '2025-08-01', 'Paid'),

('RA2', 45000.00, '2025-10-31', 'Pending'),
('RA2', 50000.00, '2025-09-15', 'Paid'),

('RA3', 2500.00, '2025-11-15', 'Pending'),
('RA3', 2500.00, '2025-09-10', 'Paid'),

('RA4', 3000.00, '2025-10-20', 'Overdue'),
('RA4', 2800.00, '2025-08-20', 'Paid'),

('RA5', 0.00, '2025-11-01', 'Paid'),
('RA5', 2500.00, '2025-09-01', 'Paid'),

('RA6', 5000.00, '2025-10-15', 'Overdue'),
('RA6', 3500.00, '2025-09-05', 'Pending'),

('RA7', 3200.00, '2025-10-10', 'Pending'),
('RA7', 1800.00, '2025-08-01', 'Paid'),

('RA8', 4100.00, '2025-10-18', 'Pending'),
('RA8', 2100.00, '2025-09-07', 'Paid');

-- 7. Parent -> Child mappings (one-to-one)
INSERT INTO parent_children (parent_id, child_id) VALUES
('P1', 'RA1'),
('P2', 'RA2'),
('P3', 'RA3'),
('P4', 'RA4'),
('P5', 'RA5'),
('P6', 'RA6'),
('P7', 'RA7'),
('P8', 'RA8');