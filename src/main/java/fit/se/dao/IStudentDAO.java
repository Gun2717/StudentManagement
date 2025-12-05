package fit.se.dao;

import fit.se.model.Student;
import java.util.List;

public interface IStudentDAO {
    boolean add(Student student) throws Exception;
    boolean update(Student student) throws Exception;
    boolean delete(String id) throws Exception;
    Student findById(String id) throws Exception;
    List<Student> findAll() throws Exception;
    List<Student> searchByName(String name) throws Exception;
    List<Student> searchByMajor(String major) throws Exception;
    List<Student> findByGpaAbove(double minGpa) throws Exception;
}
