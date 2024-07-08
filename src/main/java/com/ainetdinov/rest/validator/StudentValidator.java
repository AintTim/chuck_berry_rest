package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.ValidatorService;

import java.util.regex.Pattern;

public class StudentValidator implements ValidatorService<Student> {
    private static final String DIGIT_REGEX = "\\d";
    private static final String CAPITAL_REGEX = "[A-Z]\\w+";
    private static final String BELARUS_PHONE_REGEX = "^(\\+375) \\d{2} \\d{3}[- .]?\\d{2}[- .]?\\d{2}";
    private static final String RUSSIAN_PHONE_REGEX = "^(\\+7) \\d{3} \\d{3}[- .]?\\d{2}[- .]?\\d{2}";

    @Override
    public boolean validate(Student object) {
        return validateNameAndSurname(object, CAPITAL_REGEX, true)
                && validateNameAndSurname(object, DIGIT_REGEX, false)
                && validatePhoneNumber(object);
    }

    private boolean validateNameAndSurname(Student object, String regex, boolean isPresent) {
        Pattern pattern = Pattern.compile(regex);
        if (isPresent) {
            return pattern.matcher(object.getName()).matches() && pattern.matcher(object.getSurname()).matches();
        } else {
            return !pattern.matcher(object.getName()).matches() && !pattern.matcher(object.getSurname()).matches();
        }
    }

    private boolean validatePhoneNumber(Student object) {
        Pattern pattern = Pattern.compile(String.format("%s|%s", BELARUS_PHONE_REGEX, RUSSIAN_PHONE_REGEX));
        return pattern.matcher(object.getPhoneNumber()).matches();
    }
}
