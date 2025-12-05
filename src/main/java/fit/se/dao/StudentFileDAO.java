package fit.se.dao;

import fit.se.model.Student;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentFileDAO implements IStudentDAO {
    private final Path dataFile = Path.of("students.dat");

    @SuppressWarnings("unchecked")
    private List<Student> load() throws IOException, ClassNotFoundException {
        if (!Files.exists(dataFile)) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(dataFile))) {
            return (List<Student>) ois.readObject();
        }
    }

    private void save(List<Student> students) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(dataFile))) {
            oos.writeObject(students);
        }
    }

    @Override
    public synchronized boolean add(Student student) throws Exception {
        List<Student> students = load();
        if (students.stream().anyMatch(s -> s.getId().equals(student.getId()))) {
            throw new IllegalArgumentException("Student ID already exists: " + student.getId());
        }
        students.add(student);
        save(students);
        return true;
    }

    @Override
    public synchronized boolean update(Student student) throws Exception {
        List<Student> students = load();
        int idx = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(student.getId())) {
                idx = i;
                break;
            }
        }
        if (idx == -1) throw new IllegalArgumentException("Student not found: " + student.getId());
        students.set(idx, student);
        save(students);
        return true;
    }

    @Override
    public synchronized boolean delete(String id) throws Exception {
        List<Student> students = load();
        boolean removed = students.removeIf(s -> s.getId().equals(id));
        if (!removed) throw new IllegalArgumentException("Student not found: " + id);
        save(students);
        return true;
    }

    @Override
    public Student findById(String id) throws Exception {
        return load().stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Student> findAll() throws Exception {
        return load();
    }

    @Override
    public List<Student> searchByName(String name) throws Exception {
        String key = name.toLowerCase();
        return load().stream()
                .filter(s -> s.getFullName() != null && s.getFullName().toLowerCase().contains(key))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> searchByMajor(String major) throws Exception {
        String key = major.toLowerCase();
        return load().stream()
                .filter(s -> s.getMajor() != null && s.getMajor().toLowerCase().contains(key))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findByGpaAbove(double minGpa) throws Exception {
        return load().stream()
                .filter(s -> s.getGpa() >= minGpa)
                .sorted((a, b) -> Double.compare(b.getGpa(), a.getGpa()))
                .collect(Collectors.toList());
    }
}
