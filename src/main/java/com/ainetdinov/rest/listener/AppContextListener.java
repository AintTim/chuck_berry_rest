package com.ainetdinov.rest.listener;

import com.ainetdinov.rest.constant.Attributes;
import com.ainetdinov.rest.service.ParsingService;
import com.ainetdinov.rest.service.StudentService;
import com.ainetdinov.rest.service.StudentValidator;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Properties;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final String PROPERTIES_PATH = "/WEB-INF/resources/settings.properties";
    Properties properties = new Properties();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        initProperties(context);

        StudentService studentService = new StudentService(getResourcePath(context, "students.path"), new ParsingService(), new StudentValidator());

        context.setAttribute(Attributes.STUDENT_SERVICE, studentService);

        ServletContextListener.super.contextInitialized(sce);
    }

    private void initProperties(ServletContext context) {
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
}
