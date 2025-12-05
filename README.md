# Hệ Thống Quản Lý Sinh Viên

Ứng dụng quản lý sinh viên sử dụng Java Swing với hỗ trợ lưu trữ dữ liệu bằng File hoặc Database (MariaDB/MySQL).

## 📚 Kiến thức áp dụng

### Core Java
- ✅ **OOP**: Class, Object, Inheritance (IStudentDAO interface), Polymorphism (2 implementations)
- ✅ **Exception Handling**: Try-catch, Custom exceptions
- ✅ **Generics**: Collections với type safety
- ✅ **Collections Framework**: ArrayList, List, Stream API
- ✅ **I/O & File**: Serialization với ObjectInputStream/ObjectOutputStream
- ✅ **Thread & Concurrency**: SwingWorker, ExecutorService, Future

### Advanced Features
- ✅ **JDBC**: PreparedStatement, ResultSet, Connection pooling
- ✅ **Design Patterns**: Singleton (DatabaseConnection), DAO Pattern
- ✅ **Java 8+ Features**: Lambda, Stream API, Optional
- ✅ **Swing GUI**: JFrame, JTable, JDialog, Layout Managers

## 🚀 Cài đặt

### Yêu cầu
- Java JDK 11 trở lên
- MariaDB 10.5+ hoặc MySQL 8.0+ (nếu dùng database)
- Maven hoặc Gradle (optional)

### Thư viện cần thiết

**Tải thủ công:**

1. **MariaDB JDBC Driver** (cho database mode)
   - Download: https://mariadb.com/downloads/connectors/connectors-data-access/java8-connector/
   - File: `mariadb-java-client-3.x.x.jar`

2. **JCalendar** (cho date picker)
   - Download: https://toedter.com/jcalendar/
   - File: `jcalendar-1.4.jar`

**Hoặc dùng Maven:**

```xml
<dependencies>
    <!-- MariaDB JDBC Driver -->
    <dependency>
        <groupId>org.mariadb.jdbc</groupId>
        <artifactId>mariadb-java-client</artifactId>
        <version>3.3.0</version>
    </dependency>
    
    <!-- JCalendar for Date Picker -->
    <dependency>
        <groupId>com.toedter</groupId>
        <artifactId>jcalendar</artifactId>
        <version>1.4</version>
    </dependency>
</dependencies>
```

### Setup Database (nếu dùng Database mode)

1. **Cài đặt MariaDB/MySQL**
   ```bash
   # Windows: Download installer từ mariadb.org
   # Ubuntu/Debian:
   sudo apt-get install mariadb-server
   
   # MacOS:
   brew install mariadb
   ```

2. **Tạo Database**
   ```bash
   # Login vào MariaDB/MySQL
   mysql -u root -p
   
   # Chạy file schema.sql
   source path/to/schema.sql
   
   # Hoặc import trực tiếp
   mysql -u root -p < schema.sql
   ```

3. **Cấu hình kết nối**
   - Copy file `db.properties` vào thư mục `src/`
   - Chỉnh sửa thông tin kết nối:
   ```properties
   db.url=jdbc:mariadb://localhost:3306/student_management
   db.username=root
   db.password=your_password
   ```

## 📂 Cấu trúc thư mục

```
StudentManagement/
├── src/
│   ├── model/
│   │   └── Student.java              # Model class
│   ├── dao/
│   │   ├── IStudentDAO.java          # DAO interface
│   │   ├── StudentFileDAO.java       # File implementation
│   │   └── StudentDatabaseDAO.java   # Database implementation
│   ├── service/
│   │   └── StudentService.java       # Business logic
│   ├── ui/
│   │   ├── MainFrame.java            # Main window
│   │   └── StudentDialog.java        # Add/Edit dialog
│   ├── util/
│   │   └── DatabaseConnection.java   # DB utility
│   ├── Main.java                     # Entry point
│   └── db.properties                 # DB config
├── database/
│   └── schema.sql                    # Database schema
├── lib/                               # External JARs
│   ├── mariadb-java-client-3.x.x.jar
│   └── jcalendar-1.4.jar
└── README.md
```

## 🔧 Biên dịch và chạy

### Command Line

**Biên dịch:**
```bash
# Windows
javac -d bin -cp "lib/*" src/**/*.java src/*.java

# Linux/Mac
javac -d bin -cp "lib/*" src/**/*.java src/*.java
```

**Chạy:**
```bash
# Windows
java -cp "bin;lib/*" Main

# Linux/Mac
java -cp "bin:lib/*" Main
```

### IDE (Eclipse/IntelliJ IDEA)

1. **Eclipse:**
   - File → New → Java Project
   - Copy source code vào `src/`
   - Right-click project → Build Path → Add External JARs → Chọn các file .jar
   - Run `Main.java`

2. **IntelliJ IDEA:**
   - File → New → Project from Existing Sources
   - Copy source code vào `src/`
   - File → Project Structure → Libraries → + → Java → Chọn các file .jar
   - Run `Main.java`

### Maven

**pom.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.studentmanagement</groupId>
    <artifactId>student-management</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
            <version>3.3.0</version>
        </dependency>
        
        <dependency>
            <groupId>com.toedter</groupId>
            <artifactId>jcalendar</artifactId>
            <version>1.4</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**Chạy với Maven:**
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="Main"
```

## 💡 Sử dụng

1. **Khởi động ứng dụng**
   - Chọn phương thức lưu trữ: File hoặc Database
   - Nếu chọn Database mà không kết nối được, tự động chuyển sang File

2. **Quản lý sinh viên**
   - **Thêm mới**: Click "Thêm mới" hoặc File → New
   - **Sửa**: Chọn sinh viên → Click "Sửa" hoặc Double-click
   - **Xóa**: Chọn sinh viên → Click "Xóa"
   - **Xem chi tiết**: Chọn sinh viên → Click "Xem chi tiết"

3. **Tìm kiếm**
   - Tìm theo tên: Nhập tên → Enter
   - Tìm theo ngành: Chọn "Ngành học" → Nhập ngành
   - Tìm theo GPA: Chọn "GPA >=" → Nhập điểm

4. **Thống kê**
   - Menu File → Thống kê
   - Hiển thị tổng số SV, GPA trung bình, max, min, phân bố giới tính

## 🎯 Tính năng nổi bật

### 1. Linh hoạt lưu trữ
- **File Mode**: Sử dụng Java Serialization, dữ liệu lưu trong `students.dat`
- **Database Mode**: Kết nối MariaDB/MySQL với JDBC
- Tự động fallback sang File nếu database không khả dụng

### 2. Multi-threading
- **SwingWorker**: Load/search dữ liệu không block UI
- **ExecutorService**: Xử lý các tác vụ async
- **Progress indicator**: Hiển thị tiến trình khi xử lý

### 3. Validation mạnh mẽ
- Kiểm tra dữ liệu đầu vào
- Email validation (regex)
- Phone validation (10-11 digits)
- GPA range (0.0 - 4.0)

### 4. UX tốt
- Keyboard shortcuts (F5, Ctrl+Q, ESC)
- Double-click để edit
- Auto-focus fields
- Confirmation dialogs
- Status bar với messages

### 5. Code quality
- **Design Patterns**: DAO, Singleton
- **SOLID Principles**: Interface segregation, Dependency injection
- **Clean Code**: Meaningful names, single responsibility
- **Exception Handling**: Proper error messages

## 🐛 Troubleshooting

### Lỗi "ClassNotFoundException: org.mariadb.jdbc.Driver"
- **Nguyên nhân**: Thiếu MariaDB JDBC driver
- **Giải pháp**: Thêm `mariadb-java-client-x.x.x.jar` vào classpath

### Lỗi "Could not connect to database"
- **Nguyên nhân**: MariaDB chưa chạy hoặc config sai
- **Giải pháp**: 
  1. Kiểm tra MariaDB đang chạy: `systemctl status mariadb`
  2. Kiểm tra thông tin trong `db.properties`
  3. Test connection: `mysql -u root -p`

### Lỗi "Permission denied: students.dat"
- **Nguyên nhân**: Không có quyền ghi file
- **Giải pháp**: Chạy với quyền administrator hoặc thay đổi thư mục

### UI hiển thị lỗi font
- **Nguyên nhân**: System không hỗ trợ tiếng Việt
- **Giải pháp**: Cài đặt font Unicode (Arial, Times New Roman)

## 📝 Note quan trọng

1. **JCalendar dependency**: Nếu không tìm thấy trên Maven Central, download từ [toedter.com](https://toedter.com/jcalendar/)

2. **Database connection**: Test kết nối trước khi chạy app:
   ```bash
   mysql -u root -p
   USE student_management;
   SHOW TABLES;
   ```

3. **File mode**: File `students.dat` sẽ được tạo tự động ở thư mục chạy app

4. **Charset**: Đảm bảo database và file đều dùng UTF-8 để hỗ trợ tiếng Việt

## 🔜 Mở rộng

- [ ] Export/Import Excel
- [ ] In báo cáo PDF
- [ ] Quản lý điểm số
- [ ] Đăng nhập/phân quyền
- [ ] Upload ảnh sinh viên
- [ ] REST API
- [ ] Web interface

## 📧 Liên hệ

Nếu có thắc mắc hoặc báo lỗi, vui lòng tạo issue hoặc liên hệ qua email.

---

**License**: MIT  
**Version**: 1.0  
**Last Updated**: December 2024
