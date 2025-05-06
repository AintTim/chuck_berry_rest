package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

@Getter
public class GroupsService extends EntityService<Group> {
    private final StudentService studentService;

    public GroupsService(List<Group> groups, ValidatorService<Group> validator, StudentService studentService) {
        super(groups, validator);
        this.studentService = studentService;
    }

    @Override
    public List<Group> getEntities() {
        //TODO: Добавить обработку обновленных студентов
        synchronized (this) {
            entities.forEach(group -> {
                if (!validateStudents(group.getStudents(), true)) {
                    group.getStudents().removeIf(student -> !studentService.getEntities().contains(student));
                }
            });
        }
        return super.getEntities();
    }

    public boolean addGroup(Group group) {
        synchronized (entities) {
            if (validateEntity(group, validator::validate, this::isUnique)
                    && validateStudents(group.getStudents(), false)) {
                entities.add(group);
                return true;
            } else {
                return false;
            }
        }
    }

    public void addStudentsToGroup(List<Student> students, int groupId) {
        Group group = getEntity(g -> g.getId() == groupId);
        synchronized (entities) {
            if (validateStudents(students, false)) {
                group.getStudents().addAll(students);
            }
        }
    }

    public Group getGroupByStudentNameAndSurname(String name, String surname) {
        synchronized (entities) {
            Predicate<Group> isFound = group -> group.getStudents()
                    .stream()
                    .anyMatch(student -> student.getSurname().equals(surname) && student.getName().equals(name));
            return getEntity(isFound);
        }
    }

    private boolean validateStudents(List<Student> students, boolean currentStudents) {
        boolean isPresent = new HashSet<>(studentService.getEntities()).containsAll(students);
        if (currentStudents) {
            return isPresent;
        } else {
            boolean isRelatedToGroup = entities.stream()
                    .flatMap(g -> g.getStudents().stream())
                    .anyMatch(students::contains);
            return isPresent && !isRelatedToGroup;
        }
    }
}
