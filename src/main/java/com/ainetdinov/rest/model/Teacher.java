package com.ainetdinov.rest.model;

import com.ainetdinov.rest.constant.Subject;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class Teacher {
    private Long id;
    private String name;
    private int experience;
    private List<Subject> subjects;
}
