package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Group;

public class GroupValidator implements ValidatorService<Group>{
    @Override
    public boolean validate(Group object) {
        return false;
    }
}
