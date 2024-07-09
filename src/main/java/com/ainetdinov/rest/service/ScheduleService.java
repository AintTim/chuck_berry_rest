package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Schedule;
import lombok.Getter;

import java.nio.file.Path;
import java.util.List;

@Getter
public class ScheduleService extends EntityService<Schedule> {
    private final List<Schedule> schedules;
    private final TeacherService teacherService;
    private final GroupsService groupsService;

    public ScheduleService(Path schedulesPath, ParsingService parser, ValidatorService<Schedule> validator, TeacherService studentService, GroupsService groupsService) {
        super(parser, validator);
        this.teacherService = studentService;
        this.groupsService = groupsService;
        schedules = initEntities(schedulesPath);
    }

    @Override
    protected List<Schedule> initEntities(Path path) {
        return parser.parseList(path, Schedule.class);
    }

    @Override
    protected boolean isUnique(Schedule entity) {
        return !schedules.contains(entity);
    }
}
