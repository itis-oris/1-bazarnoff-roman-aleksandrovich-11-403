package ru.itis.studyhelper.filters;


import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@WebFilter({
        "/profile/*",
        "/requests/create",
        "/requests/*/respond",
        "/materials/upload"
})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        HttpSession session = httpRequest.getSession(false);

        String uri = httpRequest.getRequestURI();
        log.debug("AuthFilter checking: {}", uri);


        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (isLoggedIn) {
            log.debug("User is authenticated, allowing access to: {}", uri);
            filterChain.doFilter(httpRequest, httpResponse);
        } else {
            log.warn("Unauthorized access attempt to: {}", uri);
            session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", uri);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }


    }
}
