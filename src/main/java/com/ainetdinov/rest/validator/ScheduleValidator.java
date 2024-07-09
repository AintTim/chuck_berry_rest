package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.service.ValidatorService;

public class ScheduleValidator implements ValidatorService<Schedule> {
    @Override
    public boolean validate(Schedule object) {
        return false;
    }
}
