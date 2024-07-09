package com.ainetdinov.rest.service;

import lombok.Getter;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Getter
public abstract class EntityService<T> {
    protected final ParsingService parser;
    protected final ValidatorService<T> validator;

    public EntityService(ParsingService parser, ValidatorService<T> validator) {
        this.parser = parser;
        this.validator = validator;
    }

    protected abstract List<T> initEntities(Path path);

    protected abstract boolean isUnique(T entity);

    @SafeVarargs
    protected final boolean validateEntity(T entity, Predicate<T>... filters) {
        return Arrays.stream(filters).allMatch(filter -> filter.test(entity));
    }
}
