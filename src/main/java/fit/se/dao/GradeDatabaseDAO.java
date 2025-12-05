package fit.se.dao;

import fit.se.model.Grade;
import fit.se.util.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDatabaseDAO implements IGradeDAO {

    private Connection getConnection() throws SQLException, IOException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean add(Grade grade) throws Exception {
        String sql = "INSERT INTO grades (student_id, course_code, course_name, credits, " +
                "midterm_score, final_score, practice_score, total_score, letter_grade, " +
                "exam_date, semester) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setGradeParameters(pstmt, grade);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Grade grade) throws Exception {
        String sql = "UPDATE grades SET student_id=?, course_code=?, course_name=?, credits=?, " +
                "midterm_score=?, final_score=?, practice_score=?, total_score=?, " +
                "letter_grade=?, exam_date=?, semester=? WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setGradeParameters(pstmt, grade);
            pstmt.setInt(12, grade.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws Exception {
        String sql = "DELETE FROM grades WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public Grade findById(int id) throws Exception {
        String sql = "SELECT * FROM grades WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractGrade(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Grade> findByStudentId(String studentId) throws Exception {
        String sql = "SELECT * FROM grades WHERE student_id=? ORDER BY semester DESC, exam_date DESC";
        List<Grade> grades = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(extractGrade(rs));
                }
            }
        }
        return grades;
    }

    @Override
    public List<Grade> findByCourse(String courseCode) throws Exception {
        String sql = "SELECT * FROM grades WHERE course_code=? ORDER BY student_id";
        List<Grade> grades = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(extractGrade(rs));
                }
            }
        }
        return grades;
    }

    @Override
    public List<Grade> findBySemester(String semester) throws Exception {
        String sql = "SELECT * FROM grades WHERE semester=? ORDER BY student_id";
        List<Grade> grades = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, semester);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(extractGrade(rs));
                }
            }
        }
        return grades;
    }

    @Override
    public List<Grade> findAll() throws Exception {
        String sql = "SELECT * FROM grades ORDER BY student_id, semester DESC";
        List<Grade> grades = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                grades.add(extractGrade(rs));
            }
        }
        return grades;
    }

    @Override
    public double calculateGPA(String studentId) throws Exception {
        String sql = "SELECT g.total_score, g.credits FROM grades g " +
                "WHERE g.student_id=? AND g.total_score >= 4.0";

        double totalPoints = 0.0;
        int totalCredits = 0;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    double score = rs.getDouble("total_score");
                    int credits = rs.getInt("credits");

                    // Convert score to 4.0 scale
                    double gradePoint = convertToGradePoint(score);
                    totalPoints += (gradePoint * credits);
                    totalCredits += credits;
                }
            }
        }

        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }

    private double convertToGradePoint(double score) {
        if (score >= 9.0) return 4.0;
        if (score >= 8.0) return 3.5;
        if (score >= 7.0) return 3.0;
        if (score >= 6.0) return 2.5;
        if (score >= 5.0) return 2.0;
        if (score >= 4.0) return 1.0;
        return 0.0;
    }

    private Grade extractGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setId(rs.getInt("id"));
        grade.setStudentId(rs.getString("student_id"));
        grade.setCourseCode(rs.getString("course_code"));
        grade.setCourseName(rs.getString("course_name"));
        grade.setCredits(rs.getInt("credits"));
        grade.setMidtermScore(rs.getDouble("midterm_score"));
        grade.setFinalScore(rs.getDouble("final_score"));
        grade.setPracticeScore(rs.getDouble("practice_score"));
        grade.setTotalScore(rs.getDouble("total_score"));
        grade.setLetterGrade(rs.getString("letter_grade"));

        Date examDate = rs.getDate("exam_date");
        if (examDate != null) {
            grade.setExamDate(examDate.toLocalDate());
        }

        grade.setSemester(rs.getString("semester"));
        return grade;
    }

    private void setGradeParameters(PreparedStatement pstmt, Grade grade) throws SQLException {
        pstmt.setString(1, grade.getStudentId());
        pstmt.setString(2, grade.getCourseCode());
        pstmt.setString(3, grade.getCourseName());
        pstmt.setInt(4, grade.getCredits());
        pstmt.setDouble(5, grade.getMidtermScore());
        pstmt.setDouble(6, grade.getFinalScore());
        pstmt.setDouble(7, grade.getPracticeScore());
        pstmt.setDouble(8, grade.getTotalScore());
        pstmt.setString(9, grade.getLetterGrade());

        if (grade.getExamDate() != null) {
            pstmt.setDate(10, Date.valueOf(grade.getExamDate()));
        } else {
            pstmt.setNull(10, Types.DATE);
        }

        pstmt.setString(11, grade.getSemester());
    }
}
