package com.ainetdinov.rest.service;

public interface ValidatorService<T> {
    boolean validate(T object);
}
