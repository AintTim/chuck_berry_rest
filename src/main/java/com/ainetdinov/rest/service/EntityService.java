package com.ainetdinov.rest.service;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public abstract class EntityService<T> {
    protected final List<T> entities;
    protected final ValidatorService<T> validator;

    public EntityService(List<T> entities, ValidatorService<T> validator) {
        this.entities = entities;
        this.validator = validator;
    }

    public boolean isUnique(T entity) {
        return !entities.contains(entity);
    }

    public List<T> getEntities(Predicate<T> filter) {
        return entities.stream().filter(filter).collect(Collectors.toList());
    }

    public T getEntity(Predicate<T> filter) {
        return entities.stream().filter(filter).findFirst().orElse(null);
    }

    @SafeVarargs
    protected final boolean validateEntity(T entity, Predicate<T>... filters) {
        return Arrays.stream(filters).allMatch(filter -> filter.test(entity));
    }
}
