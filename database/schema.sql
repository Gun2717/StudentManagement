-- Create database
CREATE DATABASE IF NOT EXISTS student_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_management;

-- Create students table
CREATE TABLE IF NOT EXISTS students (
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

-- Create index for better performance
CREATE INDEX idx_name ON students(full_name);
CREATE INDEX idx_major ON students(major);
CREATE INDEX idx_gpa ON students(gpa);
