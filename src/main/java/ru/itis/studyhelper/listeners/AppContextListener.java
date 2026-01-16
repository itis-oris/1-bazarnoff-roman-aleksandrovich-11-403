package ru.itis.studyhelper.listeners;

import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.dao.*;
import ru.itis.studyhelper.service.*;
import ru.itis.studyhelper.utils.DatabaseUtil;
import ru.itis.studyhelper.utils.FreemarkerUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@Slf4j
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("=== Application Starting ===");
        ServletContext context = sce.getServletContext();

        try {

            log.info("Initializing Freemarker...");
            FreemarkerUtil.init();

            log.info("Initializing repositories...");
            UserDao userDao = new UserDao();
            SubjectDao subjectDao = new SubjectDao();
            HelpRequestDao helpRequestDao = new HelpRequestDao();
            ResponseDao responseDao = new ResponseDao();
            MaterialDao materialDao = new MaterialDao();
            UserSubjectDao userSubjectDao = new UserSubjectDao();

            log.info("Initializing services...");
            UserService userService = new UserService(userDao);
            SubjectService subjectService = new SubjectService(subjectDao);
            HelpRequestService helpRequestService = new HelpRequestService(helpRequestDao,
                    responseDao,
                    userDao
                );
            UserSubjectService userSubjectService = new UserSubjectService(userSubjectDao, subjectDao);
            MaterialService materialService = new MaterialService(materialDao);

            context.setAttribute("userDao", userDao);
            context.setAttribute("subjectDao", subjectDao);
            context.setAttribute("helpRequestDao", helpRequestDao);
            context.setAttribute("responseDao", responseDao);
            context.setAttribute("materialDao", materialDao);
            context.setAttribute("userSubjectDao", userSubjectDao);
            context.setAttribute("userService", userService);
            context.setAttribute("subjectService", subjectService);
            context.setAttribute("helpRequestService", helpRequestService);
            context.setAttribute("materialService", materialService);
            context.setAttribute("userSubjectService", userSubjectService);

            log.info("=== Application Started Successfully ===");
        } catch (Exception e) {
            log.error("Application initialization failed", e);
            throw new RuntimeException("Application initialization failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseUtil.close();
    }
}
