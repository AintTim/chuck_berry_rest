package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class StudentService extends EntityService<Student> {
    private final List<Student> students;

    public StudentService(Path studentsPath, ParsingService parser, ValidatorService<Student> validator) {
        super(parser, validator);
        students = initEntities(studentsPath);
    }

    public Student getStudent(int id) {
        return students.stream()
                .filter(student -> student.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Student> getStudents(Predicate<Student> filter) {
        return students.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public Student updateStudent(String jsonBody, int id) {
        Student updatedStudent = parser.parse(jsonBody, new TypeReference<>(){});
        Student currentStudent = getStudent(id);
        synchronized (students) {
            if (validateEntity(updatedStudent, Objects::nonNull, validator::validate) && validateEntity(currentStudent, Objects::nonNull)) {
                updatedStudent.setId((long) id);
                students.set(students.indexOf(currentStudent), updatedStudent);
                return updatedStudent;
            } else {
                return null;
            }
        }
    }

    public boolean addStudent(String jsonBody) {
        Student student = parser.parse(jsonBody, new TypeReference<>(){});
        synchronized (students) {
            if (validateEntity(student, Objects::nonNull, this::isUniqueStudent, validator::validate)) {
                students.add(student);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean deleteStudent(int id) {
        synchronized (students) {
            return students.removeIf(student -> student.getId() == id);
        }
    }

    private boolean isUniqueStudent(Student student) {
        return students.stream().noneMatch(s -> Objects.equals(s.getId(), student.getId()));
    }

    @Override
    protected List<Student> initEntities(Path path) {
        return parser.parseList(path, Student.class);
    }
}
