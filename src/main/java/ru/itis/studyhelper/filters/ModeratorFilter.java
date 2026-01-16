package ru.itis.studyhelper.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.User;

import java.io.IOException;

@Slf4j
@WebListener(
        "/subjects/add"
)
public class ModeratorFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);

        String uri = request.getRequestURI();
        log.debug("ModeratorFilter checking: {}", uri);


        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (user.getRole() != User.UserRole.MODERATOR) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "у вас нет прав");
            return;
        }

        filterChain.doFilter(request, response);


    }
}
