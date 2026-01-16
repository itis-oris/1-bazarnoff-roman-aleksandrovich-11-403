package ru.itis.studyhelper.controllers;

import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Subject;
import ru.itis.studyhelper.models.User;
import ru.itis.studyhelper.models.UserSubject;
import ru.itis.studyhelper.dao.UserSubjectDao;
import ru.itis.studyhelper.service.SubjectService;
import ru.itis.studyhelper.service.UserService;
import ru.itis.studyhelper.service.UserSubjectService;
import ru.itis.studyhelper.utils.FreemarkerUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@WebServlet(urlPatterns = {
        "/profile",
        "/profile/edit",
        "/users/*",
        "/profile/subjects",
        "/profile/subjects/add",
        "/profile/subjects/remove"
})
public class ProfileController extends HttpServlet {

    private UserService userService;
    private UserSubjectService userSubjectService;
    private SubjectService subjectService;

    @Override
    public void init() throws ServletException {
        userService = (UserService) getServletContext().getAttribute("userService");
        userSubjectService = (UserSubjectService) getServletContext().getAttribute("userSubjectService");
        subjectService = (SubjectService) getServletContext().getAttribute("subjectService");

        if (userSubjectService == null) {
            log.warn("UserSubjectService is null! User subjects functionality will not work.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/profile":
                showProfile(req, resp);
                break;
            case "/profile/edit":
                showEditProfile(req, resp);
                break;
            case "/profile/subjects":
                showUserSubjects(req, resp);
                break;
            default:
                if (path.startsWith("/users")) {
                    showOtherProfile(req, resp);
                } else {
                     resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/profile/edit":
                handleEditProfile(req, resp);
                break;
            case "/profile/subjects/add":
                handleAddSubject(req, resp);
                break;
            case "/profile/subjects/remove":
                handleRemoveSubject(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Профиль");
        model.put("user", user);
        model.put("isOwnProfile", true);

        if (userSubjectService != null) {
            try {
                List<UserSubjectDao.SubjectWithProficiency> userSubjects =
                        userSubjectService.getUserSubjectsWithDetails(user.getId());

                model.put("userSubjects", userSubjects);
                model.put("subjectsCount", userSubjects.size());
                model.put("helpableSubjectsCount", userSubjectService.getUserHelpableSubjectsCount(user.getId()));

            } catch (Exception e) {
                log.error("Ошибка загрузки предметов пользователя", e);
                model.put("userSubjects", List.of());
                model.put("subjectsCount", 0);
                model.put("helpableSubjectsCount", 0);
            }
        } else {
            model.put("userSubjects", List.of());
            model.put("subjectsCount", 0);
            model.put("helpableSubjectsCount", 0);
        }
        FreemarkerUtil.render("pages/profile.ftl", model, resp);
    }

    private void showOtherProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String userIDStr = pathInfo.substring(1);
        Long userID;

        try {
            userID = Long.parseLong(userIDStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<User> userOptional = userService.getUserByID(userID);

        if (userOptional.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        User user = userOptional.get();

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Профиль");
        model.put("isOwnProfile", false);
        model.put("user", user);

        if (userSubjectService != null) {
            try {
                List<UserSubjectDao.SubjectWithProficiency> userSubjects =
                        userSubjectService.getUserSubjectsWithDetails(user.getId());

                model.put("userSubjects", userSubjects);
                model.put("subjectsCount", userSubjects.size());
                model.put("helpableSubjectsCount", userSubjectService.getUserHelpableSubjectsCount(user.getId()));

            } catch (Exception e) {
                log.error("Ошибка загрузки предметов пользователя", e);
                model.put("userSubjects", List.of());
                model.put("subjectsCount", 0);
                model.put("helpableSubjectsCount", 0);
            }
        } else {
            model.put("userSubjects", List.of());
            model.put("subjectsCount", 0);
            model.put("helpableSubjectsCount", 0);
        }

        FreemarkerUtil.render("pages/profile.ftl", model, resp);
    }

    private void showEditProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Редактирование профиля");

        FreemarkerUtil.render("pages/profile-edit.ftl", model, resp);
    }

    private void handleEditProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();

        User user = (User) session.getAttribute("user");

        String fullName = req.getParameter("fullName");
        String university = req.getParameter("university");
        String faculty = req.getParameter("faculty");
        String courseStr = req.getParameter("course");
        String about = req.getParameter("about");

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Редактирование профиля");

        try {

            Integer course = null;
            if (courseStr != null && !courseStr.isBlank()) {
                course = Integer.parseInt(courseStr);
            }

            user.setFullName(fullName);
            user.setUniversity(university);
            user.setFaculty(faculty);
            user.setCourse(course);
            user.setAbout(about);

            userService.updateProfile(user);

            session.setAttribute("user", user);

            resp.sendRedirect(req.getContextPath() + "/profile");
        } catch (IllegalArgumentException e) {
            model.put("error", e.getMessage());

            FreemarkerUtil.render("pages/profile-edit.ftl", model, resp);
        } catch (Exception e) {
            model.put("error", "Ошибка обновления профиля. Попробуйте позже.");

            FreemarkerUtil.render("pages/profile-edit.ftl", model, resp);
        }
    }

    private void showUserSubjects(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User user = (User)  session.getAttribute("user");

        List<UserSubjectDao.SubjectWithProficiency> userSubjects =
                userSubjectService.getUserSubjectsWithDetails(user.getId());

        List<Subject> availableSubjects = userSubjectService.getAvailableSubjectsForUser(user.getId());

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Мои предметы");
        model.put("userSubjects", userSubjects);
        model.put("availableSubjects", availableSubjects);

        String success = req.getParameter("success");
        String error = req.getParameter("error");

        if (success != null) {
            model.put("successMessage", success);
        }

        if (error != null) {
            model.put("errorMessage", error);
        }

        FreemarkerUtil.render("pages/user-subjects.ftl", model, resp);
    }

    private void handleAddSubject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");


        String subjectIdStr = req.getParameter("subjectId");
        String proficiencyLevelStr = req.getParameter("proficiencyLevel");
        String canHelpStr = req.getParameter("canHelp");

        try {
            Long subjectId = Long.parseLong(subjectIdStr);
            UserSubject.ProficiencyLevel proficiencyLevel =
                    UserSubject.ProficiencyLevel.valueOf(proficiencyLevelStr);
            boolean canHelp = "on".equals(canHelpStr) || "true".equals(canHelpStr);

            userSubjectService.addSubjectToUser(user.getId(), subjectId, proficiencyLevel, canHelp);

            log.info("User {} added subject {}", user.getId(), subjectId);
            resp.sendRedirect(req.getContextPath() + "/profile/subjects?success=added");

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/profile/subjects?error=invalid_id");
        } catch (IllegalArgumentException e) {
            log.error("Error adding subject", e.getMessage());
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/profile/subjects?error=" + errorMsg);
        } catch (Exception e) {
            log.error("Unexpected error adding subject", e);
            resp.sendRedirect(req.getContextPath() + "/profile/subjects?error=unexpected_error");
        }
    }

    private void handleRemoveSubject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        String subjectIdStr = req.getParameter("subjectId");

        try {
            Long subjectId = Long.parseLong(subjectIdStr);
            userSubjectService.removeSubjectFromUser(user.getId(), subjectId);

            log.info("User {} removed subject {}", user.getId(), subjectId);
            resp.sendRedirect(req.getContextPath() + "/profile/subjects?success=removed");

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/profile/subjects?error=invalid_id");
        } catch (IllegalArgumentException e) {
            log.error("Error removing subject", e);
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/profile/subjects?error=" + errorMsg);
        } catch (Exception e) {
            log.error("Unexpected error removing subject", e);
            resp.sendRedirect(req.getContextPath() + "/profile/subjects?error=unexpected_error");
        }
    }

}
