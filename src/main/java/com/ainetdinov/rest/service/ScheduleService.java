package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Schedule;
import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleService extends EntityService<Schedule> {
    private final TeacherService teacherService;
    private final GroupsService groupsService;

    public ScheduleService(List<Schedule> schedules, ValidatorService<Schedule> validator, TeacherService studentService, GroupsService groupsService) {
        super(schedules, validator);
        this.teacherService = studentService;
        this.groupsService = groupsService;
    }

}
