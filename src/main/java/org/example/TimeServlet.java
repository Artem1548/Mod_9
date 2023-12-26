package org.example;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {

    private static final String DEFAULT_TIMEZONE = "UTC";
    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();

        resolver.setPrefix(getClass().getResource("/templates/").getPath());
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        Map<String, Object> params = new LinkedHashMap<>();
        String timezone = req.getParameter("timezone");
        if(timezone!=null){
            resp.addCookie(new Cookie("lastTimezone", timezone));
        }

        if (timezone == null) {
            timezone = getTimezoneValue(req.getCookies());
        }

        params.put("time" + timezone, getCurrentTime(timezone));
        Context context = new Context(req.getLocale(), Map.of("queryTime", params));
        engine.process("timeApp", context, resp.getWriter());
        resp.getWriter().close();
    }


    private String getTimezoneValue(Cookie[] cookies) {
        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lastTimezone")) {
                    return cookie.getValue();
                }
            }
        }
        return DEFAULT_TIMEZONE;
    }

    private String getCurrentTime(String timezone){
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of(timezone));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return localDateTime.format(dateTimeFormatter) + " " + timezone;
    }
}