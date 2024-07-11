package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstants;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.ScheduleService;
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

@WebServlet(SLASH + SCHEDULES + SLASH + ASTERISK)
public class ScheduleServlet extends HttpServlet {
    private ScheduleService scheduleService;
    private HttpService httpService;
    private ParsingService parsingService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        scheduleService = (ScheduleService) context.getAttribute(WebConstants.SCHEDULE_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstants.HTTP_SERVICE);
        parsingService = (ParsingService) context.getAttribute(WebConstants.PARSER_SERVICE);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        if (httpService.containsQueryString(req)) {

        } else {
            resp.getWriter().write(scheduleService.getEntities().toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private void getSchedulesByStudent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String studentSurname = req.getParameter(WebConstants.STUDENT_SURNAME);

    }

    private void getSchedulesByGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String groupNumber = req.getParameter(WebConstants.GROUP_NUMBER);
        Group group = scheduleService.getGroupsService().getEntity(g -> g.getNumber().equals(groupNumber));
        if (Objects.nonNull(group)) {
            Predicate<Schedule> isGroupPresent = schedule -> schedule.getGroupId().equals(group.getId());
            List<Schedule> schedules = scheduleService.getSchedules(isGroupPresent);
            if (!schedules.isEmpty()) {
                resp.getWriter().write(schedules.toString());
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
