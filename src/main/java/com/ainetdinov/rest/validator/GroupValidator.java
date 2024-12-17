package com.ainetdinov.rest.validator;

import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.service.ValidatorService;

import java.util.Objects;
import java.util.regex.Pattern;

public class GroupValidator implements ValidatorService<Group> {
    private static final String DIGIT_REGEX = "\\d+";

    @Override
    public boolean validate(Group object) {
        Pattern pattern = Pattern.compile(DIGIT_REGEX);
        return Objects.nonNull(object) && pattern.matcher(object.getNumber()).matches();
    }
}
