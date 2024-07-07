package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class GroupsService extends EntityService<Group> {
    private final List<Group> groups;

    public GroupsService(Path groupsPath, ParsingService parser, ValidatorService<Group> validator) {
        super(parser, validator);
        groups = initEntities(groupsPath);
    }

    public List<Group> getGroups(Predicate<Group> filter) {
        return groups.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    protected List<Group> initEntities(Path path) {
        return parser.parseList(path, Group.class);
    }
}
