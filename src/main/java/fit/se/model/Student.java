package fit.se.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Student model class demonstrating OOP principles
 */
public class Student implements Serializable, Comparable<Student> {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String id;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String phone;
    private String address;
    private String major;
    private double gpa;

    // Enum for Gender
    public enum Gender {
        NAM("Nam"), NU("Nữ"), KHAC("Khác");

        private final String displayName;

        Gender(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static Gender fromString(String text) {
            for (Gender g : Gender.values()) {
                if (g.displayName.equalsIgnoreCase(text)) {
                    return g;
                }
            }
            return NAM;
        }
    }

    // Constructors
    public Student() {}

    public Student(String id, String fullName, LocalDate dateOfBirth, Gender gender,
                   String email, String phone, String address, String major, double gpa) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.major = major;
        this.gpa = gpa;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    // Business methods
    public int getAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public String getFormattedDateOfBirth() {
        return dateOfBirth.format(DATE_FORMATTER);
    }

    public String getGradeClassification() {
        if (gpa >= 3.6) return "Xuất sắc";
        if (gpa >= 3.2) return "Giỏi";
        if (gpa >= 2.5) return "Khá";
        if (gpa >= 2.0) return "Trung bình";
        return "Yếu";
    }

    // Comparable implementation for sorting
    @Override
    public int compareTo(Student other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return String.format("Student[%s - %s - %s - GPA: %.2f]",
                id, fullName, major, gpa);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return id != null && id.equals(student.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
