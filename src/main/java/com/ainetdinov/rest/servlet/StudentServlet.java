package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstants;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.StudentService;
import com.fasterxml.jackson.core.type.TypeReference;
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

@WebServlet(SLASH + STUDENTS + SLASH + ASTERISK)
public class StudentServlet extends HttpServlet {
    private StudentService studentService;
    private HttpService httpService;
    private ParsingService parsingService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        studentService = (StudentService) context.getAttribute(WebConstants.STUDENT_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstants.HTTP_SERVICE);
        parsingService = (ParsingService) context.getAttribute(WebConstants.PARSER_SERVICE);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        httpService.prepareResponse(response);
        if (httpService.containsPath(request)) {
            getStudentById(request, response);
        } else if (httpService.containsQueryString(request)) {
            getStudentsBySurname(request, response);
        } else {
            response.getWriter().write(studentService.getEntities().toString());
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        httpService.prepareResponse(resp);
        if (studentService.addStudent(parseStudent(req))) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        httpService.prepareResponse(resp);
        if (httpService.containsPath(req)) {
            if (studentService.deleteStudent(httpService.extractId(req))) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsPath(req)) {
            Student updatesStudent = studentService.updateStudent(parseStudent(req), httpService.extractId(req));
            if (Objects.nonNull(updatesStudent)) {
                resp.getWriter().write(updatesStudent.toString());
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void getStudentById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Student student = studentService.getStudent(httpService.extractId(request));
        if (Objects.nonNull(student)) {
            response.getWriter().write(student.toString());
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void getStudentsBySurname(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String surname = request.getParameter(WebConstants.SURNAME);
        List<Student> students = studentService.getStudents(s -> s.getSurname().equals(surname));
        if (students.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.getWriter().write(students.toString());
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    public void destroy() {
    }

    private Student parseStudent(HttpServletRequest request) {
        try {
            return parsingService.parse(httpService.getRequestBody(request), new TypeReference<>(){});
        } catch (IOException e) {
            return null;
            //logs
        }
    }
}