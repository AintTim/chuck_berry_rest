package com.ainetdinov.rest.model;

import com.ainetdinov.rest.constant.Subject;
import lombok.Data;

import java.util.List;

@Data
public class Teacher {
    private Long id;
    private String name;
    private int experience;
    private List<Subject> subjects;
}
