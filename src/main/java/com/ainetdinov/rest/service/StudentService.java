package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Student;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class StudentService extends EntityService<Student> {

    public StudentService(List<Student> students, ValidatorService<Student> validator) {
        super(students, validator);
    }

    public Student getStudent(int id) {
        return entities.stream()
                .filter(student -> student.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Student> getStudents(Predicate<Student> filter) {
        return entities.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public Student updateStudent(Student updatedStudent, int id) {
//        Student updatedStudent = parser.parse(jsonBody, new TypeReference<>(){});
        Student currentStudent = getStudent(id);
        synchronized (entities) {
            if (validateEntity(updatedStudent, Objects::nonNull, validator::validate) && validateEntity(currentStudent, Objects::nonNull)) {
                updatedStudent.setId((long) id);
                entities.set(entities.indexOf(currentStudent), updatedStudent);
                return updatedStudent;
            } else {
                return null;
            }
        }
    }

    public boolean addStudent(Student student) {
        synchronized (entities) {
            if (validateEntity(student, Objects::nonNull, this::isUnique, validator::validate)) {
                entities.add(student);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean deleteStudent(int id) {
        synchronized (entities) {
            return entities.removeIf(student -> student.getId() == id);
        }
    }
}
