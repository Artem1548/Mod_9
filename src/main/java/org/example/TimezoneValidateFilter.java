package org.example;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;


@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timeZoneVal = req.getParameter("timezone");

        if(timeZoneVal==null){
            chain.doFilter(req, res);
            return;
        }

        try{
            ZoneId.of(timeZoneVal);
        } catch (RuntimeException e){
            res.setStatus(400);

            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            out.print("<html><body>");
            out.print("<h3>Invalid timezone</h3>");
            out.print("</body></html>");
            out.close();
            return;
        }

        chain.doFilter(req, res);

    }
}
