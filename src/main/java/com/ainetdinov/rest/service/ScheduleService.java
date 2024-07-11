package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Schedule;
import lombok.Getter;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class ScheduleService extends EntityService<Schedule> {
    private final TeacherService teacherService;
    private final GroupsService groupsService;

    public ScheduleService(List<Schedule> schedules, ValidatorService<Schedule> validator, TeacherService studentService, GroupsService groupsService) {
        super(schedules, validator);
        this.teacherService = studentService;
        this.groupsService = groupsService;
    }

    public List<Schedule> getSchedules(Predicate<Schedule> filter) {
        return entities.stream().filter(filter).collect(Collectors.toList());
    }
}
