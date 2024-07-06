package com.ainetdinov.rest.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Schedule {
    private LocalDate start;
    private LocalDate end;
    private Group group;
    private Teacher teacher;
}
