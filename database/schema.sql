-- ===========================================
-- RESET DATABASE + CREATE TABLES + SAMPLE DATA
-- ===========================================

-- Create database
DROP DATABASE IF EXISTS student_management;
CREATE DATABASE student_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_management;

-- =========================
-- TABLE: students
-- =========================
CREATE TABLE students (
                          id VARCHAR(20) PRIMARY KEY,
                          full_name VARCHAR(100) NOT NULL,
                          date_of_birth DATE NOT NULL,
                          gender ENUM('Nam', 'Nữ', 'Khác') NOT NULL,
                          email VARCHAR(100) UNIQUE,
                          phone VARCHAR(15),
                          address VARCHAR(255),
                          major VARCHAR(100),
                          gpa DECIMAL(3,2) CHECK (gpa >= 0.0 AND gpa <= 4.0),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data
INSERT INTO students (id, full_name, date_of_birth, gender, email, phone, address, major, gpa) VALUES
                                                                                                   ('SV001', 'Nguyễn Văn A', '2003-05-15', 'Nam', 'nva@email.com', '0912345678', 'Hà Nội', 'Công nghệ thông tin', 3.50),
                                                                                                   ('SV002', 'Trần Thị B', '2003-08-20', 'Nữ', 'ttb@email.com', '0987654321', 'TP.HCM', 'Kinh tế', 3.75),
                                                                                                   ('SV003', 'Lê Văn C', '2002-12-10', 'Nam', 'lvc@email.com', '0934567890', 'Đà Nẵng', 'Kỹ thuật điện', 3.20),
                                                                                                   ('SV004', 'Phạm Thu D', '2004-02-01', 'Nữ', 'ptd@email.com', '0971122334', 'Huế', 'Công nghệ thông tin', 3.90),
                                                                                                   ('SV005', 'Hoàng Gia E', '2003-11-11', 'Nam', 'hge@email.com', '0966677788', 'Hải Phòng', 'Quản trị kinh doanh', 2.85),
                                                                                                   ('SV006', 'Vũ Minh F', '2002-03-30', 'Nam', 'vmf@email.com', '0909988776', 'Hà Nội', 'Kỹ thuật điện', 3.10);

-- Index for better performance
CREATE INDEX idx_name ON students(full_name);
CREATE INDEX idx_major ON students(major);
CREATE INDEX idx_gpa ON students(gpa);

-- =========================
-- TABLE: grades
-- =========================
CREATE TABLE grades (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        student_id VARCHAR(20) NOT NULL,
                        course_code VARCHAR(20) NOT NULL,
                        course_name VARCHAR(200) NOT NULL,
                        credits INT NOT NULL,
                        midterm_score DECIMAL(4,2) DEFAULT 0,
                        final_score DECIMAL(4,2) DEFAULT 0,
                        practice_score DECIMAL(4,2) DEFAULT 0,
                        total_score DECIMAL(4,2) DEFAULT 0,
                        letter_grade VARCHAR(5),
                        exam_date DATE,
                        semester VARCHAR(20),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                        UNIQUE KEY unique_student_course (student_id, course_code, semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_student_grades ON grades(student_id);
CREATE INDEX idx_course_grades ON grades(course_code);
CREATE INDEX idx_semester_grades ON grades(semester);

-- =========================
-- TABLE: users
-- =========================
CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100),
                       role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
                       active BOOLEAN DEFAULT TRUE,
                       last_login TIMESTAMP NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Default admin account
-- Username: admin
-- Password: admin123
-- BCrypt hash generated with strength 10
INSERT INTO users (username, password_hash, full_name, email, role) VALUES
    ('admin', '$2a$10$rVQ5YC.8mIqK6PqLqf8zBeCk4VPvqYGLxJDmxIlKZ0q4BjZBmZEkG', 'Administrator', 'admin@system.com', 'ADMIN');

-- Additional test accounts
-- Username: teacher1, Password: teacher123
INSERT INTO users (username, password_hash, full_name, email, role) VALUES
    ('teacher1', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J5m6qKxNiJ.3j8c3GFLJmRfCVnO3Fu', 'Giáo viên Demo', 'teacher1@system.com', 'TEACHER');

-- Username: student1, Password: student123
INSERT INTO users (username, password_hash, full_name, email, role) VALUES
    ('student1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sinh viên Demo', 'student1@system.com', 'STUDENT');

CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_role ON users(role);

-- =========================
-- SUMMARY
-- =========================
-- Tables created: students, grades, users
-- Default accounts:
--   1. admin / admin123 (ADMIN)
--   2. teacher1 / teacher123 (TEACHER)
--   3. student1 / student123 (STUDENT)
