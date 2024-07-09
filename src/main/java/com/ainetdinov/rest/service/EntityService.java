package com.ainetdinov.rest.service;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Getter
public abstract class EntityService<T> {
    protected final List<T> entities;
    protected final ValidatorService<T> validator;

    public EntityService(List<T> entities, ValidatorService<T> validator) {
        this.entities = entities;
        this.validator = validator;
    }

    protected boolean isUnique(T entity) {
        return !entities.contains(entity);
    }

    @SafeVarargs
    protected final boolean validateEntity(T entity, Predicate<T>... filters) {
        return Arrays.stream(filters).allMatch(filter -> filter.test(entity));
    }
}
