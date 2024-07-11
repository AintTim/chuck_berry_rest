package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.model.Teacher;
import lombok.Getter;

import java.util.List;
import java.util.Objects;


@Getter
public class TeacherService extends EntityService<Teacher> {

    public TeacherService(List<Teacher> teachers, ValidatorService<Teacher> validator) {
        super(teachers, validator);
    }

    public boolean addTeacher(Teacher teacher) {
        synchronized (entities) {
            if (validateEntity(teacher, Objects::nonNull, this::isUnique, validator::validate)) {
                entities.add(teacher);
                return true;
            } else {
                return false;
            }
        }
    }

    public Teacher getTeacher(int id) {
        synchronized (entities) {
            return entities.stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
    }

    public List<Subject> updateTeacherSubjects(List<Subject> subjects, int id) {
        Teacher teacher = getTeacher(id);
        synchronized (entities) {
            if (!subjects.isEmpty() && Objects.nonNull(teacher)) {
                teacher.setSubjects(subjects);
                return teacher.getSubjects();
            } else {
                return null;
            }
        }
    }
}
