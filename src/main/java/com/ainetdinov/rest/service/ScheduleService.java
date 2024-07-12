package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Teacher;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
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

    public Schedule updateSchedule(Schedule current, Schedule updated) {
        synchronized (entities) {
            if (validateScheduleUpdate(current, updated)) {
                entities.set(entities.indexOf(current), updated);
                return updated;
            } else {
                return null;
            }
        }
    }

    private boolean validateScheduleUpdate(Schedule current, Schedule updated) {
        return validateEntity(current, validator::validate, entities::contains, this::validateTeacherAndGroupPresence)
                && validateEntity(updated, validator::validate, this::isUnique, this::validateTeacherAndGroupPresence);
    }

    public boolean addSchedule(Schedule schedule) {
        synchronized (this) {
            if (validateEntity(schedule, validator::validate, this::isUnique, this::validateTeacherAndGroupPresence)) {
                entities.add(schedule);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean validateTeacherAndGroupPresence(Schedule schedule) {
        Teacher teacher = teacherService.getEntity(t -> Objects.equals(t.getId(), schedule.getTeacherId()));
        Group group = groupsService.getEntity(t -> Objects.equals(t.getId(), schedule.getGroupId()));
        return Objects.nonNull(teacher) && Objects.nonNull(group);
    }
}
