package com.ainetdinov.rest.servlet;

import com.ainetdinov.rest.constant.WebConstants;
import com.ainetdinov.rest.service.HttpService;
import com.ainetdinov.rest.service.ScheduleService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.ainetdinov.rest.constant.Endpoint.*;

@WebServlet(SLASH + SCHEDULES + SLASH + ASTERISK)
public class ScheduleServlet extends HttpServlet {
    private ScheduleService scheduleService;
    private HttpService httpService;

    @Override
    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        scheduleService = (ScheduleService) context.getAttribute(WebConstants.SCHEDULE_SERVICE);
        httpService = (HttpService) context.getAttribute(WebConstants.HTTP_SERVICE);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        httpService.prepareResponse(resp);
        resp.getWriter().write(scheduleService.getEntities().toString());
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
