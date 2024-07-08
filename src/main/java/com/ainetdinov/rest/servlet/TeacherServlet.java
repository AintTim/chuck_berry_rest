package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.Subject;
import com.ainetdinov.rest.constant.WebConstants;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.TeacherService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.ainetdinov.rest.constant.Endpoint.*;

@WebServlet(SLASH + TEACHERS + SLASH + ASTERISK)
public class TeacherServlet extends HttpServlet {
    private TeacherService teacherService;
    private HttpService httpService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        teacherService = (TeacherService) context.getAttribute(WebConstants.TEACHER_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstants.HTTP_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        resp.getWriter().write(teacherService.getTeachers().toString());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        String body = httpService.getRequestBody(req);
        if (teacherService.addTeacher(body)) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsPath(req)) {
            List<Subject> subjects = teacherService.updateTeacherSubjects(req.getParameterValues(WebConstants.SUBJECTS), httpService.extractId(req));
            if (Objects.nonNull(subjects)) {
                resp.getWriter().write(subjects.toString());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
