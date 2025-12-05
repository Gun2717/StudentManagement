package fit.se.dao;

import fit.se.model.Student;
import fit.se.util.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database-based DAO implementation
 * Demonstrates: JDBC, Exception Handling, SQL
 */
public class StudentDatabaseDAO implements IStudentDAO {
    private Connection getConnection() throws SQLException, IOException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean add(Student student) throws Exception {
        String sql = "INSERT INTO students (id, full_name, date_of_birth, gender, " +
                "email, phone, address, major, gpa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setStudentParameters(pstmt, student);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Student ID already exists: " + student.getId());
        }
    }

    @Override
    public boolean update(Student student) throws Exception {
        String sql = "UPDATE students SET full_name=?, date_of_birth=?, gender=?, " +
                "email=?, phone=?, address=?, major=?, gpa=? WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getFullName());
            pstmt.setDate(2, Date.valueOf(student.getDateOfBirth()));
            pstmt.setString(3, student.getGender().getDisplayName());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getAddress());
            pstmt.setString(7, student.getMajor());
            pstmt.setDouble(8, student.getGpa());
            pstmt.setString(9, student.getId());

            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Student not found: " + student.getId());
            }
            return true;
        }
    }

    @Override
    public boolean delete(String id) throws Exception {
        String sql = "DELETE FROM students WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();

            if (rows == 0) {
                throw new IllegalArgumentException("Student not found: " + id);
            }
            return true;
        }
    }

    @Override
    public Student findById(String id) throws Exception {
        String sql = "SELECT * FROM students WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudent(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Student> findAll() throws Exception {
        String sql = "SELECT * FROM students ORDER BY id";
        List<Student> students = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(extractStudent(rs));
            }
        }
        return students;
    }

    @Override
    public List<Student> searchByName(String name) throws Exception {
        String sql = "SELECT * FROM students WHERE full_name LIKE ? ORDER BY full_name";
        List<Student> students = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudent(rs));
                }
            }
        }
        return students;
    }

    @Override
    public List<Student> searchByMajor(String major) throws Exception {
        String sql = "SELECT * FROM students WHERE major LIKE ? ORDER BY major, full_name";
        List<Student> students = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + major + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudent(rs));
                }
            }
        }
        return students;
    }

    @Override
    public List<Student> findByGpaAbove(double minGpa) throws Exception {
        String sql = "SELECT * FROM students WHERE gpa >= ? ORDER BY gpa DESC";
        List<Student> students = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, minGpa);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudent(rs));
                }
            }
        }
        return students;
    }

    /**
     * Extract Student object from ResultSet
     */
    private Student extractStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getString("id"));
        student.setFullName(rs.getString("full_name"));
        student.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        student.setGender(Student.Gender.fromString(rs.getString("gender")));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setAddress(rs.getString("address"));
        student.setMajor(rs.getString("major"));
        student.setGpa(rs.getDouble("gpa"));
        return student;
    }

    /**
     * Set PreparedStatement parameters from Student object
     */
    private void setStudentParameters(PreparedStatement pstmt, Student student)
            throws SQLException {
        pstmt.setString(1, student.getId());
        pstmt.setString(2, student.getFullName());
        pstmt.setDate(3, Date.valueOf(student.getDateOfBirth()));
        pstmt.setString(4, student.getGender().getDisplayName());
        pstmt.setString(5, student.getEmail());
        pstmt.setString(6, student.getPhone());
        pstmt.setString(7, student.getAddress());
        pstmt.setString(8, student.getMajor());
        pstmt.setDouble(9, student.getGpa());
    }
}
