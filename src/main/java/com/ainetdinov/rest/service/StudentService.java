package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentService {
    private final ParsingService parser;
    private final ValidatorService<Student> validator;
    @Getter
    private final List<Student> students;

    public StudentService(Path studentsPath, ParsingService parser, ValidatorService<Student> validator) {
        this.validator = validator;
        this.parser = parser;
        students = parser.parseList(studentsPath, Student.class);
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
            if (validateStudent(updatedStudent, Objects::nonNull, validator::validate) && validateStudent(currentStudent, Objects::nonNull)) {
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
            if (validateStudent(student, Objects::nonNull, this::isUniqueStudent, validator::validate)) {
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

    @SafeVarargs
    private boolean validateStudent(Student student, Predicate<Student>... filters) {
        return Arrays.stream(filters).allMatch(filter -> filter.test(student));
    }

    private boolean isUniqueStudent(Student student) {
        return students.stream().noneMatch(s -> Objects.equals(s.getId(), student.getId()));
    }
}
