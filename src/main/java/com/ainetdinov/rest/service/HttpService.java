package com.ainetdinov.rest.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ainetdinov.rest.constant.Endpoint.SLASH;

public class HttpService {
    private final String EMPTY_STRING = "";

    public int extractId(HttpServletRequest request) {
        return Integer.parseInt(request.getPathInfo().replace(SLASH, EMPTY_STRING));
    }

    public boolean containsQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return Objects.nonNull(queryString) && !queryString.isEmpty();
    }

    public boolean containsPath(HttpServletRequest request) {
        String path = request.getPathInfo();
        return Objects.nonNull(path) && !path.replace(SLASH, EMPTY_STRING).isEmpty();
    }

    public void prepareResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");
    }

    public String getRequestBody(HttpServletRequest request) throws IOException {
        return request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
