package fit.se.service;

import fit.se.dao.*;
import fit.se.model.Student;
import java.util.List;
import java.util.concurrent.*;

/**
 * Service layer - Business logic and threading support
 */
public class StudentService {
    private IStudentDAO dao;
    private ExecutorService executorService;

    public StudentService(IStudentDAO dao) {
        this.dao = dao;
        this.executorService = Executors.newFixedThreadPool(3);
    }

    /**
     * Add student with validation
     */
    public boolean addStudent(Student student) throws Exception {
        validateStudent(student);
        return dao.add(student);
    }

    /**
     * Update student with validation
     */
    public boolean updateStudent(Student student) throws Exception {
        validateStudent(student);
        return dao.update(student);
    }

    /**
     * Delete student
     */
    public boolean deleteStudent(String id) throws Exception {
        return dao.delete(id);
    }

    /**
     * Find student by ID
     */
    public Student findStudentById(String id) throws Exception {
        return dao.findById(id);
    }

    /**
     * Get all students
     */
    public List<Student> getAllStudents() throws Exception {
        return dao.findAll();
    }

    /**
     * Search students by name
     */
    public List<Student> searchByName(String name) throws Exception {
        return dao.searchByName(name);
    }

    /**
     * Search students by major
     */
    public List<Student> searchByMajor(String major) throws Exception {
        return dao.searchByMajor(major);
    }

    /**
     * Get top students by GPA
     */
    public List<Student> getTopStudents(double minGpa) throws Exception {
        return dao.findByGpaAbove(minGpa);
    }

    /**
     * Async operation - Get all students in background thread
     */
    public Future<List<Student>> getAllStudentsAsync() {
        return executorService.submit(() -> dao.findAll());
    }

    /**
     * Async operation - Search by name in background thread
     */
    public Future<List<Student>> searchByNameAsync(String name) {
        return executorService.submit(() -> dao.searchByName(name));
    }

    /**
     * Calculate statistics
     */
    public StudentStatistics calculateStatistics() throws Exception {
        List<Student> students = dao.findAll();
        return new StudentStatistics(students);
    }

    /**
     * Validate student data
     */
    private void validateStudent(Student student) throws IllegalArgumentException {
        if (student.getId() == null || student.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sinh viên không được để trống");
        }

        if (student.getFullName() == null || student.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }

        if (student.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Ngày sinh không được để trống");
        }

        if (student.getGpa() < 0.0 || student.getGpa() > 4.0) {
            throw new IllegalArgumentException("GPA phải trong khoảng 0.0 - 4.0");
        }

        // Email validation
        if (student.getEmail() != null && !student.getEmail().isEmpty()) {
            if (!student.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Email không hợp lệ");
            }
        }

        // Phone validation
        if (student.getPhone() != null && !student.getPhone().isEmpty()) {
            if (!student.getPhone().matches("^[0-9]{10,11}$")) {
                throw new IllegalArgumentException("Số điện thoại phải có 10-11 chữ số");
            }
        }
    }

    /**
     * Shutdown executor service
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    /**
     * Inner class for statistics
     */
    public static class StudentStatistics {
        private int totalStudents;
        private double averageGpa;
        private double maxGpa;
        private double minGpa;
        private long maleCount;
        private long femaleCount;

        public StudentStatistics(List<Student> students) {
            this.totalStudents = students.size();

            if (!students.isEmpty()) {
                this.averageGpa = students.stream()
                        .mapToDouble(Student::getGpa)
                        .average()
                        .orElse(0.0);

                this.maxGpa = students.stream()
                        .mapToDouble(Student::getGpa)
                        .max()
                        .orElse(0.0);

                this.minGpa = students.stream()
                        .mapToDouble(Student::getGpa)
                        .min()
                        .orElse(0.0);

                this.maleCount = students.stream()
                        .filter(s -> s.getGender() == Student.Gender.NAM)
                        .count();

                this.femaleCount = students.stream()
                        .filter(s -> s.getGender() == Student.Gender.NU)
                        .count();
            }
        }

        // Getters
        public int getTotalStudents() { return totalStudents; }
        public double getAverageGpa() { return averageGpa; }
        public double getMaxGpa() { return maxGpa; }
        public double getMinGpa() { return minGpa; }
        public long getMaleCount() { return maleCount; }
        public long getFemaleCount() { return femaleCount; }

        @Override
        public String toString() {
            return String.format(
                    "Tổng số SV: %d | TB GPA: %.2f | Max: %.2f | Min: %.2f | Nam: %d | Nữ: %d",
                    totalStudents, averageGpa, maxGpa, minGpa, maleCount, femaleCount
            );
        }
    }
}
