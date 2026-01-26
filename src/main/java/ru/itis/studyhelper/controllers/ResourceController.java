package ru.itis.studyhelper.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet("/static/*")
public class ResourceController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        InputStream resourceStream = getClass().getResourceAsStream("/static" + path);

        if (resourceStream == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (path.endsWith(".css")) {
            resp.setContentType("text/css;charset=UTF-8");
        } else if (path.endsWith(".js")) {
            resp.setContentType("application/javascript;charset=UTF-8");
        } else if (path.endsWith(".png")) {
            resp.setContentType("image/png");
        }

        try (resourceStream; OutputStream out = resp.getOutputStream()) {
            resourceStream.transferTo(out);
        }
    }
}
