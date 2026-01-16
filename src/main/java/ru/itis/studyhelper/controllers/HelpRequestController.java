package ru.itis.studyhelper.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.HelpRequest;
import ru.itis.studyhelper.models.Response;
import ru.itis.studyhelper.models.Subject;
import ru.itis.studyhelper.models.User;
import ru.itis.studyhelper.service.HelpRequestService;
import ru.itis.studyhelper.service.SubjectService;
import ru.itis.studyhelper.utils.FreemarkerUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@WebServlet(urlPatterns = {
        "/requests",
        "/requests/create",
        "/requests/*"
})
public class HelpRequestController extends HttpServlet {


    private HelpRequestService helpRequestService;
    private SubjectService subjectService;

    @Override
    public void init() throws ServletException {
        helpRequestService = (HelpRequestService) getServletContext().getAttribute("helpRequestService");
        subjectService = (SubjectService) getServletContext().getAttribute("subjectService");

        if (helpRequestService == null) {
            log.error("HelpRequestService is NULL!");
        }
        if (subjectService == null) {
            log.error("SubjectService is NULL!");
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();


        log.debug("GET request to: {}", path);

        if ("/requests".equals(path)) {

            if (pathInfo == null || pathInfo.equals("/")) {
                showRequestList(req, resp);
            } else {
                try {
                    Long id = Long.parseLong(pathInfo.substring(1));
                    showRequestDetail(req, resp, id);
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid request ID");
                }
            }

        } else if ("/requests/create".equals(path)) {
            showCreateRequestForm(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();

        log.debug("POST request to: {} {}", path, pathInfo);

        if ("/requests/create".equals(path)) {
            handleCreateRequest(req, resp);
        } else if (pathInfo != null) {
            try {
                Long id = Long.parseLong(pathInfo.substring(1).split("/")[0]);
                String action = pathInfo.contains("/respond") ? "respond" :
                        pathInfo.contains("/accept") ? "accept" : null;

                if ("respond".equals(action)) {
                    handleAddResponse(req, resp, id);
                } else if ("accept".equals(action)) {
                    handleAcceptResponse(req, resp, id);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (Exception e) {
                log.error("Error parsing request path", e);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showRequestList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String statusFilter  = req.getParameter("status");
        String subjectIdFilter = req.getParameter("subject");
        String searchQuery = req.getParameter("search");

        List<HelpRequest> helpRequests;

        if (searchQuery != null && !searchQuery.isBlank()) {
            helpRequests = helpRequestService.searchHelpRequest(searchQuery);
        } else if (statusFilter != null && !statusFilter.isBlank()) {
            try {

                HelpRequest.RequestStatus status = HelpRequest.RequestStatus.valueOf(statusFilter);
                helpRequests = helpRequestService.getHelpRequestsByStatus(status);

            } catch (IllegalArgumentException e) {
                helpRequests = helpRequestService.getAllHelpRequests();
            }
        } else if (subjectIdFilter != null && !subjectIdFilter.isBlank()) {
            try {
                Long id = Long.parseLong(subjectIdFilter);

                helpRequests = helpRequestService.getHelpRequestsBySubject(id);

            } catch (NumberFormatException e) {
                helpRequests = helpRequestService.getAllHelpRequests();
            }
        } else {
            helpRequests = helpRequestService.getAllHelpRequests();
        }

        List<Subject> subjects = subjectService.getAllSubjects();

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Запрос помощи");
        model.put("requests", helpRequests);
        model.put("subjects", subjects);
        model.put("statusFilter", statusFilter);
        model.put("searchQuery", searchQuery);
        model.put("subjectFilter", subjectIdFilter);

        FreemarkerUtil.render("pages/request.ftl", model, resp);
    }


    private void showCreateRequestForm(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Subject> subjects = subjectService.getAllSubjects();

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Создать запрос помощи");
        model.put("subjects", subjects);
        model.put("user", user);


        FreemarkerUtil.render("pages/request-create.ftl", model, resp);
    }

    private void handleCreateRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        String subjectIDStr = req.getParameter("subjectId");
        String title = req.getParameter("title");
        String description = req.getParameter("description");

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Создать запрос помощи");
        model.put("user", user);

        try {
            Long subjectID = Long.parseLong(subjectIDStr);
            HelpRequest helpRequest = helpRequestService.createHelpRequest(
                    user.getId(), subjectID, title, description
            );

            log.info("Help request created: {}", helpRequest.getId());

            resp.sendRedirect(req.getContextPath() + "/requests/" + helpRequest.getId());
        } catch (NumberFormatException e) {

            List<Subject> subjects = subjectService.getAllSubjects();
            model.put("subjects", subjects);
            model.put("error", "Необходимо выбрать предмет");
            model.put("title", title);
            model.put("description", description);
            model.put("user", user);

            FreemarkerUtil.render("pages/request-create.ftl", model, resp);

        } catch (IllegalArgumentException e) {
            log.warn("Help request creation validation error: {}", e.getMessage());

            List<Subject> subjects = subjectService.getAllSubjects();
            model.put("subjects", subjects);
            model.put("error", e.getMessage());
            model.put("subjectId", subjectIDStr);
            model.put("title", title);
            model.put("description", description);
            model.put("user", user);

            FreemarkerUtil.render("pages/request-create.ftl", model, resp);

        } catch (Exception e) {
            log.error("Help request creation error", e);

            List<Subject> subjects = subjectService.getAllSubjects();
            model.put("subjects", subjects);
            model.put("error", "Ошибка создания запроса. Попробуйте позже.");

            FreemarkerUtil.render("pages/request-create.ftl", model, resp);
        }
    }


    private void showRequestDetail(HttpServletRequest req, HttpServletResponse resp, Long id) throws IOException {
        log.info("showRequestDetail called with id={}", id);

        Optional<HelpRequest> helpRequestOpt = helpRequestService.getHelpRequestByID(id);

        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        log.info("User from session: {}", user != null ? user.getEmail() : "null");


        log.info("helpRequest exist: {}", helpRequestOpt.isEmpty());
        if (helpRequestOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Request not found");
            return;
        }

        HelpRequest helpRequest = helpRequestOpt.get();
        List<Response> responses = helpRequestService.getResponsesForRequest(id);

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", helpRequest.getTitle());
        model.put("helpRequest", helpRequest);
        model.put("responses", responses);
        model.put("user", user);

        try {
            FreemarkerUtil.render("pages/request-details.ftl", model, resp);
        } catch (Exception e) {
            log.error("Freemarker rendering error for request id={}", id, e);
            resp.setContentType("text/plain; charset=UTF-8");
            resp.getWriter().write("Ошибка рендеринга шаблона:\n" + e.toString());
        }
    }



    private void handleAddResponse(HttpServletRequest req, HttpServletResponse resp, Long helpRequestID) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        String message = req.getParameter("message");

        log.info("Add response to request {} by user {}", helpRequestID, user.getEmail());

        try {
            helpRequestService.addResponse(helpRequestID, user.getId(), message);
            log.info("Response added successfully");
            resp.sendRedirect(req.getContextPath() + "/requests/" + helpRequestID);
        } catch (IllegalArgumentException e) {
            log.warn("Add response validation error: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/requests/" + helpRequestID + "?error=" + e.getMessage());
        } catch (Exception e) {
            log.error("Add response error", e);
            resp.sendRedirect(req.getContextPath() + "/requests/" + helpRequestID + "?error=Ошибка добавления ответа");
        }
    }

    private void handleAcceptResponse(HttpServletRequest req, HttpServletResponse resp, Long helpRequestID) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        String responseIDStr = req.getParameter("responseID");

        log.info("Accept response {} for request {} by user {}", responseIDStr, helpRequestID, user.getEmail());

        try {
            Long responseID = Long.parseLong(responseIDStr);

            helpRequestService.acceptResponse(helpRequestID, responseID, user.getId());

            resp.sendRedirect(req.getContextPath() + "/requests/" + helpRequestID);
        } catch (NumberFormatException e) {
            log.warn("Invalid response ID: {}", responseIDStr);
            resp.sendRedirect(req.getContextPath() + "/requests/" + helpRequestID);
        } catch (IllegalArgumentException e) {
            log.warn("Accept response validation error: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/requests/" + helpRequestID + "?error=" + e.getMessage());
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/requests/" + helpRequestID + "?error=Ошибка принятия ответа");
        }
    }
}
