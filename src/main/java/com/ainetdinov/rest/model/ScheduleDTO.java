package com.ainetdinov.rest.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ScheduleDTO {
    private Schedule current;
    private Schedule updated;
}
