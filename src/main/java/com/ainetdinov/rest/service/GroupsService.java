package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class GroupsService extends EntityService<Group> {
    private final List<Group> groups;
    private final ValidatorService<Student> studentValidator;

    public GroupsService(Path groupsPath, ParsingService parser, ValidatorService<Group> validator, ValidatorService<Student> studentValidator) {
        super(parser, validator);
        this.studentValidator = studentValidator;
        groups = initEntities(groupsPath);
    }

    public List<Group> getGroups(Predicate<Group> filter) {
        return groups.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public boolean addGroup(String jsonBody) {
        Group group = parser.parse(jsonBody, new TypeReference<>(){});
        synchronized (groups) {
            if (validateEntity(group, Objects::nonNull, this::isUniqueGroup, validator::validate) && validateGroupStudents(group)) {
                groups.add(group);
                return true;
            } else {
                return false;
            }
        }
    }

    public void addStudentsToGroup(String jsonBody, int groupId) {
        List<Student> students = parser.parse(jsonBody, new TypeReference<>(){});
        Group group = getGroupById(groupId);
        synchronized (groups) {
            students.forEach(student -> {
                if (studentValidator.validate(student) && isUniqueStudent(student, group)) {
                    group.getStudents().add(student);
                }
            });
        }
    }

    public Group getGroupById(int groupId) {
        return groups.stream()
                .filter(group -> group.getId() == groupId)
                .findFirst()
                .orElse(null);
    }

    private boolean isUniqueStudent(Student student, Group group) {
        return !group.getStudents().contains(student);
    }

    private boolean isUniqueGroup(Group group) {
        return groups.stream().noneMatch(g -> g.getNumber().equalsIgnoreCase(group.getNumber()));
    }

    @Override
    protected List<Group> initEntities(Path path) {
        return parser.parseList(path, Group.class);
    }

    private boolean validateGroupStudents(Group group) {
        return group.getStudents().stream().allMatch(studentValidator::validate);
    }
}
