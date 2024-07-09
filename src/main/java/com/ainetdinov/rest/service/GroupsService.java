package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class GroupsService extends EntityService<Group> {
    private final List<Group> groups;
    private final StudentService studentService;

    public GroupsService(Path groupsPath, ParsingService parser, ValidatorService<Group> validator, StudentService studentService) {
        super(parser, validator);
        this.studentService = studentService;
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
            if (validateEntity(group, Objects::nonNull, this::isUnique, validator::validate) && validateStudentsPresence(group.getStudents())) {
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
            if (validateStudentsPresence(students)) {
                students.stream()
                        .filter(s -> isUniqueStudent(s, group))
                        .forEach(group.getStudents()::add);
            }
        }
    }

    public Group getGroupById(int groupId) {
        return groups.stream()
                .filter(group -> group.getId() == groupId)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected List<Group> initEntities(Path path) {
        return parser.parseList(path, Group.class);
    }

    @Override
    protected boolean isUnique(Group entity) {
        return !groups.contains(entity);
    }

    private boolean isUniqueStudent(Student student, Group group) {
        return !group.getStudents().contains(student);
    }

    private boolean validateStudentsPresence(List<Student> students) {
        return new HashSet<>(studentService.getStudents()).containsAll(students);
    }
}
