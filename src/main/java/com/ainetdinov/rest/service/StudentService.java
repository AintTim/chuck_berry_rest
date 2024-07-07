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
public class StudentService {
    private static final String STUDENTS_PATH = "students.path";
    private final ParsingService parser;
    private final ValidatorService<Student> validator;
    private final List<Student> students;

    public StudentService(Path studentsPath, ParsingService parser, ValidatorService<Student> validator) {
        this.validator = validator;
        this.parser = parser;
        students = parser.parse(studentsPath, Student.class);
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

    public boolean addStudent(String jsonBody) {
        Student student = parser.parse(jsonBody, new TypeReference<>(){});
        synchronized (students) {
            if (validateStudent(student)) {
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

    private boolean validateStudent(Student student) {
        return Objects.nonNull(student)
                && isUniqueStudent(student)
                && validator.validate(student);
    }

    private boolean isUniqueStudent(Student student) {
        return students.stream().noneMatch(s -> Objects.equals(s.getId(), student.getId()));
    }
}
