-- schema.sql: explicit DDL for student_management_system
-- NOTE: Do NOT include CREATE DATABASE or USE when Spring is configured with a datasource.
-- Ensure the database 'student_management_system' exists before running the app.

-- Drop child tables first to avoid foreign key errors
DROP TABLE IF EXISTS parent_children;
DROP TABLE IF EXISTS fees;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;

-- ----------------------------------------
-- Students Table
-- ----------------------------------------
CREATE TABLE students (
    student_id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    major VARCHAR(50) NOT NULL,
    grade INT NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15),
    date_of_birth DATE,

    -- NEW optional profile fields added to match the Student entity
    address VARCHAR(255),
    program VARCHAR(100),
    year INT,
    advisor VARCHAR(100),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------
-- Courses Table
-- ----------------------------------------
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) UNIQUE,
    credits INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------
-- Enrollments Table
-- ----------------------------------------
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(50) NOT NULL,
    course_id INT NOT NULL,
    enrollment_date DATE NOT NULL DEFAULT (CURDATE()),
    grade VARCHAR(2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE(student_id, course_id)
);

-- ----------------------------------------
-- Fees Table
-- ----------------------------------------
CREATE TABLE fees (
    fee_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE,
    status ENUM('Paid', 'Pending', 'Overdue') NOT NULL DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- ----------------------------------------
-- Parent-Children Mapping Table
-- ----------------------------------------
-- Using a surrogate id column keeps JPA mapping simple (no composite @IdClass required).
CREATE TABLE parent_children (
    id INT PRIMARY KEY AUTO_INCREMENT,
    parent_id VARCHAR(50) NOT NULL,
    child_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (child_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Optional index to speed up lookups by parent
CREATE INDEX idx_parent_children_parent ON parent_children(parent_id);

-- ----------------------------------------
-- Optional Indexes for Faster Queries
-- ----------------------------------------
CREATE INDEX idx_student_email ON students(email);
CREATE INDEX idx_course_code ON courses(course_code);
CREATE INDEX idx_enrollment_student ON enrollments(student_id);
CREATE INDEX idx_fees_student ON fees(student_id);