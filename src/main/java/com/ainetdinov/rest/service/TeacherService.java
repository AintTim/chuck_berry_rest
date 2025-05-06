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
            if (validateEntity(teacher, validator::validate, this::isUnique)) {
                entities.add(teacher);
                return true;
            } else {
                return false;
            }
        }
    }

    public List<Subject> updateTeacherSubjects(List<Subject> subjects, int id) {
        Teacher teacher = getEntity(t -> t.getId() == id);
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
