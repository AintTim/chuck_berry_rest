package com.ainetdinov.rest.constant;

import java.util.Arrays;
import java.util.Objects;

public enum Subject {
    MATH, MUSIC, ART, HISTORY, GEOGRAPHY, SOCIAL_STUDIES, PHYSICAL_EDUCATION, UNKNOWN;

    public static Subject getSubject(String name) {
        if (Objects.isNull(name) || name.isEmpty()) {
            return null;
        }
        return Arrays.stream(Subject.values())
                .filter(subject -> subject.name().equals(name.toUpperCase()))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
