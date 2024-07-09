package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.constant.WebConstants;
import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.TeacherService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ainetdinov.rest.constant.Endpoint.*;

@WebServlet(SLASH + TEACHERS + SLASH + ASTERISK)
public class TeacherServlet extends HttpServlet {
    private TeacherService teacherService;
    private HttpService httpService;
    private ParsingService parsingService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        teacherService = (TeacherService) context.getAttribute(WebConstants.TEACHER_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstants.HTTP_SERVICE);
        parsingService = (ParsingService) context.getAttribute(WebConstants.PARSER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        resp.getWriter().write(teacherService.getEntities().toString());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        httpService.prepareResponse(resp);
        if (teacherService.addTeacher(parseTeacher(req))) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsPath(req)) {
            List<Subject> subjects = teacherService.updateTeacherSubjects(parseTeacherSubjects(req), httpService.extractId(req));
            if (Objects.nonNull(subjects)) {
                resp.getWriter().write(subjects.toString());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private Teacher parseTeacher(HttpServletRequest request) {
        try {
            return parsingService.parse(httpService.getRequestBody(request), new TypeReference<>(){});
        } catch (IOException e) {
            return null;
        }
    }

    private List<Subject> parseTeacherSubjects(HttpServletRequest request) {
        return Arrays.stream(request.getParameterValues(WebConstants.SUBJECTS))
                .map(Subject::getSubject)
                .collect(Collectors.toList());
    }
}
