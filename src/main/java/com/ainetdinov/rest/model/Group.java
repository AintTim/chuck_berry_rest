package com.ainetdinov.rest.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class Group {
    private Long id;
    private String number;
    private List<Student> students;
}
