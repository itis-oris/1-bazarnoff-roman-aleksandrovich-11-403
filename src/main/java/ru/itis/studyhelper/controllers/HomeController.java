package ru.itis.studyhelper.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.itis.studyhelper.models.User;
import ru.itis.studyhelper.utils.FreemarkerUtil;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = {"", "/"})
public class HomeController extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = null;

        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Главная");

        if (user != null) {
            model.put("user", user);
        }

        FreemarkerUtil.render("pages/index.ftl", model, resp);

    }
}
