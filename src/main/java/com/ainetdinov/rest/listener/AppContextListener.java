package com.ainetdinov.rest.listener;

import com.ainetdinov.rest.constant.WebConstants;
import com.ainetdinov.rest.model.Group;
import com.ainetdinov.rest.model.Schedule;
import com.ainetdinov.rest.model.Student;
import com.ainetdinov.rest.model.Teacher;
import com.ainetdinov.rest.service.*;
import com.ainetdinov.rest.validator.GroupValidator;
import com.ainetdinov.rest.validator.ScheduleValidator;
import com.ainetdinov.rest.validator.StudentValidator;
import com.ainetdinov.rest.validator.TeacherValidator;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final String PROPERTIES_PATH = "/WEB-INF/resources/settings.properties";
    private final Properties properties = new Properties();
    private final ParsingService parsingService = new ParsingService();
    private ServletContext context;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
        initProperties();

        StudentService studentService = new StudentService(initEntities(WebConstants.STUDENTS_PATH, Student.class), new StudentValidator());
        TeacherService teacherService = new TeacherService(initEntities(WebConstants.TEACHERS_PATH, Teacher.class), new TeacherValidator());
        GroupsService groupsService = new GroupsService(initEntities(WebConstants.GROUPS_PATH, Group.class), new GroupValidator(), studentService);
        ScheduleService scheduleService = new ScheduleService(initEntities(WebConstants.SCHEDULES_PATH, Schedule.class), new ScheduleValidator(), teacherService, groupsService);

        context.setAttribute(WebConstants.PARSER_SERVICE, parsingService);
        context.setAttribute(WebConstants.STUDENT_SERVICE, studentService);
        context.setAttribute(WebConstants.TEACHER_SERVICE, teacherService);
        context.setAttribute(WebConstants.GROUP_SERVICE, groupsService);
        context.setAttribute(WebConstants.SCHEDULE_SERVICE, scheduleService);
        context.setAttribute(WebConstants.HTTP_SERVICE, new HttpService());

        ServletContextListener.super.contextInitialized(sce);
    }

    private void initProperties() {
        try {
            properties.load(context.getResourceAsStream(PROPERTIES_PATH));
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error reading properties file: %s", PROPERTIES_PATH), e);
        }
    }

    private Path getResourcePath(ServletContext context, String path) {
        try {
            return Path.of(context.getResource(properties.getProperty(path)).toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> initEntities(String path, Class<T> clazz) {
        return parsingService.parseList(getResourcePath(context, path), clazz);
    }
}
