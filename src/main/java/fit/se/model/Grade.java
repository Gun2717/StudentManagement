package fit.se.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Grade model for managing student scores
 */
public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String studentId;
    private String courseCode;
    private String courseName;
    private int credits;
    private double midtermScore;
    private double finalScore;
    private double practiceScore;
    private double totalScore;
    private String letterGrade;
    private LocalDate examDate;
    private String semester;

    public Grade() {}

    public Grade(String studentId, String courseCode, String courseName, int credits) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
    }

    // Calculate total score (30% midterm + 10% practice + 60% final)
    public void calculateTotalScore() {
        this.totalScore = (midtermScore * 0.3) + (practiceScore * 0.1) + (finalScore * 0.6);
        this.letterGrade = calculateLetterGrade(totalScore);
    }

    // Calculate letter grade
    private String calculateLetterGrade(double score) {
        if (score >= 9.0) return "A+";
        if (score >= 8.5) return "A";
        if (score >= 8.0) return "B+";
        if (score >= 7.0) return "B";
        if (score >= 6.5) return "C+";
        if (score >= 5.5) return "C";
        if (score >= 5.0) return "D+";
        if (score >= 4.0) return "D";
        return "F";
    }

    // Get grade point for GPA calculation
    public double getGradePoint() {
        return switch (letterGrade) {
            case "A+", "A" -> 4.0;
            case "B+" -> 3.5;
            case "B" -> 3.0;
            case "C+" -> 2.5;
            case "C" -> 2.0;
            case "D+" -> 1.5;
            case "D" -> 1.0;
            default -> 0.0;
        };
    }

    // Check if passed
    public boolean isPassed() {
        return totalScore >= 4.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public double getMidtermScore() { return midtermScore; }
    public void setMidtermScore(double midtermScore) {
        this.midtermScore = midtermScore;
        calculateTotalScore();
    }

    public double getFinalScore() { return finalScore; }
    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
        calculateTotalScore();
    }

    public double getPracticeScore() { return practiceScore; }
    public void setPracticeScore(double practiceScore) {
        this.practiceScore = practiceScore;
        calculateTotalScore();
    }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    @Override
    public String toString() {
        return String.format("Grade[%s - %s: %.2f (%s)]",
                studentId, courseCode, totalScore, letterGrade);
    }
}
