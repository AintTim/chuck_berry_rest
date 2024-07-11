package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.ScheduleQuery;
import com.ainetdinov.rest.constant.WebConstants;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Teacher;
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
import java.time.LocalDate;
import java.util.ArrayList;
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
        List<Schedule> schedules;
        if (httpService.containsQueryString(req)) {
            if (Objects.nonNull(req.getParameter(WebConstants.STUDENT_SURNAME))) {
               schedules = getSchedulesByStudent(req);
            } else if (Objects.nonNull(req.getParameter(WebConstants.GROUP_NUMBER))) {
                schedules = getSchedulesByGroup(req);
            } else {
                schedules = getSchedulesByTeacher(req);
            }
        } else {
            schedules = scheduleService.getEntities();
        }
        sendResponse(req, resp, schedules);
    }

    private List<Schedule> getSchedulesBy(Object object, ScheduleQuery model) {
        List<Schedule> schedules = new ArrayList<>();
        if (Objects.nonNull(object)) {
            Predicate<Schedule> filter = switch (model) {
                case GROUP, STUDENT -> schedule -> schedule.getGroupId().equals(((Group) object).getId());
                case TEACHER -> schedule -> schedule.getTeacherId().equals(((Teacher) object).getId());
                case SCHEDULE -> schedule -> schedule.getStart().toLocalDate().equals(object);
            };
            schedules = scheduleService.getSchedules(filter);
        }
        return schedules;
    }

    private void sendResponse(HttpServletRequest req, HttpServletResponse resp, List<Schedule> schedules) throws IOException {
        if (schedules.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            resp.getWriter().write(schedules.toString());
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private List<Schedule> getSchedulesByStudent(HttpServletRequest req) {
        String studentSurname = req.getParameter(WebConstants.STUDENT_SURNAME);
        String studentName = req.getParameter(WebConstants.STUDENT_NAME);
        Group group = scheduleService.getGroupsService().getGroupByStudentNameAndSurname(studentName, studentSurname);
        return getSchedulesBy(group, ScheduleQuery.STUDENT);
    }

    private List<Schedule> getSchedulesByGroup(HttpServletRequest req) {
        String groupNumber = req.getParameter(WebConstants.GROUP_NUMBER);
        Group group = scheduleService.getGroupsService().getEntity(g -> g.getNumber().equals(groupNumber));
        return getSchedulesBy(group, ScheduleQuery.GROUP);
    }

    private List<Schedule> getSchedulesByTeacher(HttpServletRequest req) {
        String teacherName = req.getParameter(WebConstants.TEACHER_NAME);
        Teacher teacher = scheduleService.getTeacherService().getEntity(t -> t.getName().equals(teacherName));
        return getSchedulesBy(teacher, ScheduleQuery.TEACHER);
    }
}
