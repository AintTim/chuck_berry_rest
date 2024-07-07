package com.ainetdinov.rest.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ParsingService {
    private final ObjectMapper mapper;

    public ParsingService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ParsingService() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public <T> List<T> parse(Path jsonPath, Class<T> clazz) {
        try {
            CollectionType returnType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            if (Files.exists(jsonPath)) {
                return mapper.readValue(jsonPath.toFile(), returnType);
            } else {
                throw new FileNotFoundException(String.format("File %s not found", jsonPath));
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error reading file %s from %s", jsonPath, e));
        }
    }

    public <T> T parse(String jsonBody, TypeReference<T> typeReference) {
        try {
            if (isValidJson(jsonBody)) {
                return mapper.readValue(jsonBody, typeReference);
            } else {
                throw new IllegalArgumentException(String.format("Invalid json body: %s", jsonBody));
            }
        } catch (IOException e) {
            return null;
//            throw new RuntimeException(String.format("Error reading json body from %s", jsonBody));
        }
    }

    private boolean isValidJson(String jsonBody) {
        ObjectMapper validator = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        try {
            validator.readTree(jsonBody);
        } catch (JacksonException e) {
            return false;
        }
        return true;
    }
}
