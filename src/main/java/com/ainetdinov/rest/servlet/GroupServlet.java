package com.ainetdinov.rest.servlet;

import static com.ainetdinov.rest.constant.Endpoint.*;

import com.ainetdinov.rest.constant.Attributes;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.service.GroupsService;
import com.ainetdinov.rest.service.HttpService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@WebServlet(SLASH + GROUPS + SLASH + ASTERISK)
public class GroupServlet extends HttpServlet {
    private GroupsService groupsService;
    private HttpService httpService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        groupsService = (GroupsService) context.getAttribute(Attributes.GROUP_SERVICE);
        httpService = (HttpService) context.getAttribute(Attributes.HTTP_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsQueryString(req)) {
            if (Objects.nonNull(req.getParameter("number"))) {
                getGroupByNumber(req, resp);
            } else {
                getGroupsByStudentSurname(req, resp);
            }
        } else {
            resp.getWriter().write(groupsService.getGroups().toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private void getGroupByNumber(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = req.getParameter("number");
        List<Group> groups = groupsService.getGroups(g -> g.getNumber().equals(number));
        if (groups.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            resp.getWriter().write(groups.toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private void getGroupsByStudentSurname(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String studentSurname = req.getParameter("surname");
        Predicate<Group> isStudentPresent = g -> g.getStudents().stream()
                .allMatch(student -> student.getSurname().equals(studentSurname));
        List<Group> groups = groupsService.getGroups(isStudentPresent);
        if (groups.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            resp.getWriter().write(groups.toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
