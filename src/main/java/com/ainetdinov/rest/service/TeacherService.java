package com.ainetdinov.rest.service;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.model.Teacher;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Getter
public class TeacherService extends EntityService<Teacher> {
    private final List<Teacher> teachers;

    public TeacherService(Path teachersPath, ParsingService parser, ValidatorService<Teacher> validator) {
        super(parser, validator);
        teachers = initEntities(teachersPath);
    }

    public boolean addTeacher(String jsonBody) {
        Teacher teacher = parser.parse(jsonBody, new TypeReference<>() {
        });
        synchronized (teachers) {
            if (validateEntity(teacher, Objects::nonNull, this::isUniqueTeacher, validator::validate)) {
                teachers.add(teacher);
                return true;
            } else {
                return false;
            }
        }
    }

    public Teacher getTeacher(int id) {
        synchronized (teachers) {
            return teachers.stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
    }

    public List<Subject> updateTeacherSubjects(String[] subjects, int id) {
        List<Subject> updatedSubjects = Arrays.stream(subjects)
                .map(Subject::getSubject)
                .collect(Collectors.toList());
        Teacher teacher = getTeacher(id);
        synchronized (teachers) {
            if (!updatedSubjects.isEmpty()) {
                teacher.setSubjects(updatedSubjects);
                return teacher.getSubjects();
            } else {
                return null;
            }
        }
    }

    private boolean isUniqueTeacher(Teacher teacher) {
        return teachers.stream().noneMatch(t -> Objects.equals(t.getId(), teacher.getId()));
    }

    @Override
    protected List<Teacher> initEntities(Path path) {
        return parser.parseList(path, Teacher.class);
    }
}
