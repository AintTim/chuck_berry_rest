package com.ainetdinov.rest.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Student {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String phoneNumber;
}
