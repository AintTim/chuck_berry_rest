package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.service.ValidatorService;

import java.util.regex.Pattern;

public class TeacherValidator implements ValidatorService<Teacher> {
    private static final String DIGIT_REGEX = "\\d";
    private static final String CAPITAL_REGEX = "[A-Z]\\w+";

    @Override
    public boolean validate(Teacher object) {
        return validateName(object, CAPITAL_REGEX, true) && validateName(object, DIGIT_REGEX, false) && object.getExperience() >= 0 && !object.getSubjects().isEmpty();
    }

    private boolean validateName(Teacher object, String regex, boolean isPresent) {
        Pattern pattern = Pattern.compile(regex);
        if (isPresent) {
            return pattern.matcher(object.getName()).matches();
        } else {
            return !pattern.matcher(object.getName()).matches();
        }
    }
}
