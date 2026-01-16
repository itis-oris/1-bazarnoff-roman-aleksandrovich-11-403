package ru.itis.studyhelper.controllers;


import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.User;
import ru.itis.studyhelper.service.UserService;
import ru.itis.studyhelper.utils.FileUploadUtil;
import ru.itis.studyhelper.utils.FreemarkerUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.util.Map;

@Slf4j
@WebServlet(urlPatterns = {"/register", "/login", "/logout"})
public class AuthController extends HttpServlet {

    private UserService userService;


    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        log.info("AuthController initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/register":
                showRegisterPage(req, resp);
                break;
            case "/login":
                showLoginPage(req, resp);
                break;
            case "/logout":
                handleLogout(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/register":
                handleRegister(req, resp);
                break;
            case "/login":
                handleLogin(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showRegisterPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Регистрация");
        FreemarkerUtil.render("pages/register.ftl", model, resp);
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String passwordConfirm = req.getParameter("passwordConfirm");
        String fullName = req.getParameter("fullName");
        String university = req.getParameter("university");
        String faculty = req.getParameter("faculty");
        String courseStr = req.getParameter("course");


        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Регистрация");

        try {
            if (!password.equals(passwordConfirm)) {
                throw new IllegalArgumentException("Пароли не совпадают");
            }

            Integer course = 1;
            if (courseStr != null && !courseStr.isBlank()) {
                course = Integer.parseInt(courseStr);
            }

            User user = userService.register(email, password, fullName, university, faculty, course);
            log.info("User registered successfully: {}", email);

            HttpSession session = req.getSession();
            session.setAttribute("user", user);

            resp.sendRedirect(req.getContextPath() + "/profile");
        } catch (IllegalArgumentException e ) {
            model.put("error", e.getMessage());
            model.put("email", email);
            model.put("fullName", fullName);
            model.put("university", university);
            model.put("faculty", faculty);
            model.put("course", courseStr);

            FreemarkerUtil.render("pages/register.ftl", model, resp);
        } catch (Exception e) {
            log.error("Registration error", e);
            FreemarkerUtil.render("pages/register.ftl", model, resp);
        }
    }


    private void showLoginPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Вход");
        FreemarkerUtil.render("pages/login.ftl", model, resp);
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Вход");

        try {
            User user = userService.authenticate(email, password);

            HttpSession session = req.getSession();
            session.setAttribute("user", user);

            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");

            if (redirectUrl != null) {
                session.removeAttribute("redirectAfterLogin");
                resp.sendRedirect(redirectUrl);
            } else {
                resp.sendRedirect(req.getContextPath() + "/profile");
            }
        } catch (UserService.AuthenticationException e) {
            model.put("error", "Неверный email или пароль");
            model.put("email", email);

            FreemarkerUtil.render("pages/login.ftl", model, resp);
        } catch (Exception e) {
            log.error("Login error", e);

            model.put("error", "Ошибка входа в систему. Попробуйте позже");
            model.put("email", email);

            FreemarkerUtil.render("pages/login.ftl", model, resp);
        }
    }


    private void handleLogout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                log.info("User logged out: {}", user.getEmail());
            }
            session.invalidate();
        }

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
