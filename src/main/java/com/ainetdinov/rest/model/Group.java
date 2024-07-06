package com.ainetdinov.rest.model;

import lombok.Data;

import java.util.List;

@Data
public class Group {
    private String number;
    private List<Student> students;
}
