package com.ainetdinov.rest.servlet;

import static com.ainetdinov.rest.constant.Endpoint.*;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.ainetdinov.rest.constant.Attributes;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.StudentService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(SLASH + STUDENTS + SLASH + ASTERISK)
public class StudentServlet extends HttpServlet {
    private StudentService studentService;
    private HttpService httpService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        studentService = (StudentService) context.getAttribute(Attributes.STUDENT_SERVICE);
        httpService = (HttpService) context.getAttribute(Attributes.HTTP_SERVICE);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        httpService.prepareResponse(response);
        if (httpService.containsPath(request)) {
            getStudentById(request, response);
        } else if (httpService.containsQueryString(request)) {
            getStudentsBySurname(request, response);
        } else {
            response.getWriter().write(studentService.getStudents().toString());
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        httpService.prepareResponse(resp);
        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        if (studentService.addStudent(body)) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsPath(req)) {
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Student updatesStudent = studentService.updateStudent(requestBody, httpService.extractId(req));
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
        String surname = request.getParameter("surname");
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
}