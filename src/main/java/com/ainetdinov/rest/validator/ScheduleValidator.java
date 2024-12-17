package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.service.ValidatorService;

import java.time.LocalDateTime;
import java.util.Objects;

public class ScheduleValidator implements ValidatorService<Schedule> {
    private final LocalDateTime currentDateTime = LocalDateTime.now();

    @Override
    public boolean validate(Schedule object) {
        return Objects.nonNull(object)
                && validateDates(object)
                && validateIds(object);
    }

    private boolean validateDates(Schedule object) {
        return object.getStart().isAfter(currentDateTime)
                && object.getEnd().isAfter(object.getStart());
    }

    private boolean validateIds(Schedule object) {
        return object.getTeacherId() > 0
                && object.getGroupId() > 0;
    }
}
