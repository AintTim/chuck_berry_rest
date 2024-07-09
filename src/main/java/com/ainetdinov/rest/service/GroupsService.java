package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class GroupsService extends EntityService<Group> {
    private final StudentService studentService;

    public GroupsService(List<Group> groups, ValidatorService<Group> validator, StudentService studentService) {
        super(groups, validator);
        this.studentService = studentService;
    }

    public List<Group> getGroups(Predicate<Group> filter) {
        return entities.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public boolean addGroup(Group group) {
        synchronized (entities) {
            if (validateEntity(group, Objects::nonNull, this::isUnique, validator::validate) && validateStudentsPresence(group.getStudents())) {
                entities.add(group);
                return true;
            } else {
                return false;
            }
        }
    }

    public void addStudentsToGroup(List<Student> students, int groupId) {
        Group group = getGroupById(groupId);
        synchronized (entities) {
            if (validateStudentsPresence(students)) {
                students.stream()
                        .filter(s -> isUniqueStudent(s, group))
                        .forEach(group.getStudents()::add);
            }
        }
    }

    public Group getGroupById(int groupId) {
        return entities.stream()
                .filter(group -> group.getId() == groupId)
                .findFirst()
                .orElse(null);
    }


    private boolean isUniqueStudent(Student student, Group group) {
        return !group.getStudents().contains(student);
    }

    private boolean validateStudentsPresence(List<Student> students) {
        return new HashSet<>(studentService.getEntities()).containsAll(students);
    }
}
