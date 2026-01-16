package ru.itis.studyhelper.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Subject;
import ru.itis.studyhelper.models.User;
import ru.itis.studyhelper.service.SubjectService;
import ru.itis.studyhelper.utils.FreemarkerUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@WebServlet(urlPatterns = {"/subjects", "/subjects/add", "/subjects/*"})
public class SubjectController extends HttpServlet {

    private SubjectService subjectService;

    @Override
    public void init() throws ServletException {
        subjectService = (SubjectService) getServletContext().getAttribute("subjectService");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("SubjectController doGet path={} pathInfo={}", req.getServletPath(), req.getPathInfo());
        String path = req.getServletPath();

        if ("/subjects".equals(path)) {
            showSubjectList(req, resp);
        } else if ("/subjects/add".equals(path)) {
            showAddSubjectForm(req, resp);
        } else {
            String pathInfo = req.getPathInfo();

            if (pathInfo != null && pathInfo.length() > 1) {
                try {
                    Long id = Long.parseLong(pathInfo.substring(1));
                    showSubjectDetails(req, resp, id);
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid subject ID");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/subjects/add".equals(path)) {
            handleAddSubject(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    private void showSubjectList(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String searchQuery = req.getParameter("search");
        String facultyFilter = req.getParameter("filter");
        String semesterStr = req.getParameter("semester");

        List<Subject> subjects;

        if (searchQuery != null && !searchQuery.isBlank()) {
            subjects = subjectService.searchSubjects(searchQuery);
        } else if (facultyFilter != null && !facultyFilter.isBlank()) {
            subjects = subjectService.getSubjectsByFaculty(facultyFilter);
        } else if (semesterStr != null && !semesterStr.isBlank()) {
            try {
                Integer semester = Integer.parseInt(semesterStr);
                subjects = subjectService.getSubjectsBySemester(semester);
            } catch (NumberFormatException e) {
                subjects = subjectService.getAllSubjects();
            }
        } else {
            subjects = subjectService.getAllSubjects();
        }

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Предметы");
        model.put("subjects", subjects);
        model.put("searchQuery", searchQuery);
        model.put("facultyFilter", facultyFilter);
        model.put("semesterFilter", semesterStr);

        FreemarkerUtil.render("pages/subjects.ftl", model, resp);
    }

    private void showAddSubjectForm(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Добавить предмет");

        FreemarkerUtil.render("pages/subject-add.ftl", model, resp);
    }

    private void showSubjectDetails(HttpServletRequest req, HttpServletResponse resp, Long id) throws IOException {
        Optional<Subject> subjectOpt = subjectService.getSubjectByID(id);

        if (subjectOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Subject not found");
            return;
        }

        Subject subject = subjectOpt.get();

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", subject.getName());
        model.put("subject", subject);

        FreemarkerUtil.render("pages/subject-details.ftl", model, resp);
    }

    private void handleAddSubject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();

        User user = (User) session.getAttribute("user");

        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String faculty = req.getParameter("faculty");
        String semesterStr = req.getParameter("semester");

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Добавить предмет");

        try {
            Integer semester = null;
            if (semesterStr != null && !semesterStr.isBlank()) {
                semester = Integer.parseInt(semesterStr);
            }

            Subject subject = subjectService.createSubject(
                    name, description, faculty, semester, user.getId()
            );


            resp.sendRedirect(req.getContextPath() + "/subjects/" + subject.getId());
        } catch (IllegalArgumentException e) {
            model.put("error", e.getMessage());
            model.put("name", name);
            model.put("description", description);
            model.put("faculty", faculty);
            model.put("semester", semesterStr);

            FreemarkerUtil.render("pages/subject-add.ftl", model, resp);

        } catch (Exception e) {
            model.put("error", "Ошибка создания предмета. Попробуйте позже.");

            FreemarkerUtil.render("pages/subject-add.ftl", model, resp);

        }

    }
}
