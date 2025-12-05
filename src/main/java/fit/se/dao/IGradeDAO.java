package fit.se.dao;

import fit.se.model.Grade;
import java.util.List;

public interface IGradeDAO {
    boolean add(Grade grade) throws Exception;
    boolean update(Grade grade) throws Exception;
    boolean delete(int id) throws Exception;
    Grade findById(int id) throws Exception;
    List<Grade> findByStudentId(String studentId) throws Exception;
    List<Grade> findByCourse(String courseCode) throws Exception;
    List<Grade> findBySemester(String semester) throws Exception;
    List<Grade> findAll() throws Exception;
    double calculateGPA(String studentId) throws Exception;
}
