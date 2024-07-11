package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstants;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.service.GroupsService;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
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
import java.util.function.Predicate;

import static com.ainetdinov.rest.constant.Endpoint.*;

@WebServlet(SLASH + GROUPS + SLASH + ASTERISK)
public class GroupServlet extends HttpServlet {
    private GroupsService groupsService;
    private HttpService httpService;
    private ParsingService parsingService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        groupsService = (GroupsService) context.getAttribute(WebConstants.GROUP_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstants.HTTP_SERVICE);
        parsingService = (ParsingService) context.getAttribute(WebConstants.PARSER_SERVICE);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsQueryString(req)) {
            if (Objects.nonNull(req.getParameter(WebConstants.NUMBER))) {
                getGroupByNumber(req, resp);
            } else {
                getGroupsByStudentSurname(req, resp);
            }
        } else {
            resp.getWriter().write(groupsService.getEntities().toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        httpService.prepareResponse(resp);
        if (groupsService.addGroup(parseGroup(req))) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        int groupId = httpService.extractId(req);
        groupsService.addStudentsToGroup(parseStudents(req), groupId);
        resp.getWriter().write(groupsService.getEntity(g -> g.getId() == groupId).toString());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void getGroupByNumber(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String number = req.getParameter(WebConstants.NUMBER);
        Group group = groupsService.getEntity(g -> g.getNumber().equals(number));
        if (Objects.isNull(group)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            resp.getWriter().write(group.toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private void getGroupsByStudentSurname(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String studentSurname = req.getParameter(WebConstants.SURNAME);
        Predicate<Group> isStudentPresent = g -> g.getStudents().stream()
                .anyMatch(student -> student.getSurname().equalsIgnoreCase(studentSurname));
        List<Group> groups = groupsService.getEntities(isStudentPresent);
        if (groups.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            resp.getWriter().write(groups.toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private Group parseGroup(HttpServletRequest request) {
        try {
            return parsingService.parse(httpService.getRequestBody(request), new TypeReference<>(){});
        } catch (IOException e) {
            return null;
            //logs
        }
    }

    private List<Student> parseStudents(HttpServletRequest request) {
        try {
            return parsingService.parse(httpService.getRequestBody(request), new TypeReference<>(){});
        } catch (IOException e) {
            return null;
        }
    }
}
