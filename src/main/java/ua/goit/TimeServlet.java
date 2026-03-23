package ua.goit;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String timezoneParam = req.getParameter("timezone");
        String lastTimezone = "UTC";

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                    lastTimezone = cookie.getValue();
                }
            }
        }
 
        String timezoneToUse = (timezoneParam != null && !timezoneParam.isEmpty()) 
                               ? timezoneParam.replace(" ", "+") 
                               : lastTimezone;

        Cookie tzCookie = new Cookie("lastTimezone", timezoneToUse);
        tzCookie.setMaxAge(60 * 60 * 24); // Живе 1 день
        resp.addCookie(tzCookie);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(timezoneToUse));
        String formattedTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " " + timezoneToUse;

        Context context = new Context();
        context.setVariable("currentTime", formattedTime);

        resp.setContentType("text/html;charset=UTF-8");
        engine.process("time", context, resp.getWriter());
    }
}