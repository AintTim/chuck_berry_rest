package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;
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
            if (validateEntity(group, validator::validate) && validateGroupStudents(group)) {
                groups.add(group);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    protected List<Group> initEntities(Path path) {
        return parser.parseList(path, Group.class);
    }

    private boolean validateGroupStudents(Group group) {
        return group.getStudents().stream().allMatch(studentValidator::validate);
    }
}
