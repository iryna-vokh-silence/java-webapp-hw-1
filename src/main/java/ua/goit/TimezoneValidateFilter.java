package ua.goit;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TimeZone;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        
        String timezone = req.getParameter("timezone");

        if (timezone != null && !timezone.isEmpty()) {
            String tzId = timezone.replace(" ", "+");
            
            boolean isValid = false;
            for (String id : TimeZone.getAvailableIDs()) {
                if (id.equalsIgnoreCase(tzId)) {
                    isValid = true;
                    break;
                }
            }

            if (!isValid) {
                res.setStatus(400); // Помилка 400
                res.setContentType("text/html;charset=UTF-8");
                res.getWriter().write("Invalid timezone");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}