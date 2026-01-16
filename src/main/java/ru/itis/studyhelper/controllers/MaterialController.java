package ru.itis.studyhelper.controllers;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Material;
import ru.itis.studyhelper.models.Subject;
import ru.itis.studyhelper.models.User;
import ru.itis.studyhelper.service.MaterialService;
import ru.itis.studyhelper.service.SubjectService;
import ru.itis.studyhelper.utils.FileUploadUtil;
import ru.itis.studyhelper.utils.FreemarkerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@WebServlet(urlPatterns = {
        "/materials",
        "/materials/upload",
        "/materials/download/*"
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 20
)
public class MaterialController extends HttpServlet {

    private MaterialService materialService;
    private SubjectService subjectService;

    @Override
    public void init() throws ServletException {
        materialService = (MaterialService) getServletContext().getAttribute("materialService");
        subjectService = (SubjectService) getServletContext().getAttribute("subjectService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/materials".equals(path)) {
            showMaterialsList(req, resp);
        } else if ("/materials/upload".equals(path)) {
            showUploadForm(req, resp);
        } else if (path.startsWith("/materials/download")) {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No ID");
                return;
            }
            try {
                Long id = Long.parseLong(pathInfo.substring(1));
                handleDownload(req, resp, id);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/materials/upload".equals(path)) {
            handleUpload(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    private void showMaterialsList(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String subjectIDStr = req.getParameter("subject");
        String searchQuery = req.getParameter("search");

        List<Material> materials;

        if (searchQuery != null &&  !searchQuery.isBlank()) {
            materials = materialService.searchMaterials(searchQuery);
        } else if (subjectIDStr != null && !subjectIDStr.isBlank()) {
            try {
                Long subjectID = Long.parseLong(subjectIDStr);
                materials = materialService.getMaterialsBySubject(subjectID);
            } catch (NumberFormatException e) {
                materials = materialService.getAllMaterials();
            }
        } else {
            materials = materialService.getAllMaterials();
        }

        List<Subject> subjects = subjectService.getAllSubjects();

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Материалы");
        model.put("materials", materials);
        model.put("subjects", subjects);
        model.put("subjectFilter", subjectIDStr);
        model.put("searchQuery", searchQuery);

        FreemarkerUtil.render("pages/materials.ftl", model, resp);
    }

    private void showUploadForm(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Subject> subjects = subjectService.getAllSubjects();

        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Загрузить материал");
        model.put("subjects", subjects);

        FreemarkerUtil.render("pages/material-upload.ftl", model, resp);
    }

    private void handleUpload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");


        String subjectIDStr = req.getParameter("subjectID");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String typeStr = req.getParameter("materialType");



        Map<String, Object> model = FreemarkerUtil.createModel(req);
        model.put("title", "Загрузить материал");


        try {
            Long subjectID = Long.parseLong(subjectIDStr);
            Material.MaterialType type = Material.MaterialType.valueOf(typeStr);

            String filePath = null;

            if (type != Material.MaterialType.VIDEO_LINK) {
                Part filePart = req.getPart("file");
                if (filePart == null || filePart.getSize() == 0) {
                    throw new IllegalArgumentException("Файл не выбран");
                }

                filePath = FileUploadUtil.saveMaterial(req, filePart);
            } else {
                filePath = req.getParameter("videoUrl");
                if (filePath == null || filePath.isBlank()) {
                    throw new IllegalArgumentException("Ссылка на видео не указана");
                }
            }


            Material material = materialService.uploadMaterial(
                    user.getId(), subjectID, title, description, type, filePath
            );

            log.info("Material uploaded: {}", material.getId());

            resp.sendRedirect(req.getContextPath() + "/materials");
        } catch (NumberFormatException e) {
            List<Subject> subjects = subjectService.getAllSubjects();

            model.put("subjects", subjects);
            model.put("error", "Необходимо выбрать предмет");
            FreemarkerUtil.render("pages/material-upload.ftl", model, resp);
        } catch (IllegalArgumentException e) {
            List<Subject> subjects = subjectService.getAllSubjects();
            model.put("subjects", subjects);
            model.put("error", e.getMessage());
            model.put("title", title);
            model.put("description", description);
            FreemarkerUtil.render("pages/material-upload.ftl", model, resp);
        } catch (Exception e) {
            log.error("Material upload error", e);
            List<Subject> subjects = subjectService.getAllSubjects();
            model.put("subjects",subjects);
            model.put("error", "Ошибка загрузки файла");
            FreemarkerUtil.render("pages/material-upload.ftl", model, resp);
        }
    }


    private void handleDownload(HttpServletRequest req, HttpServletResponse resp, Long materialID) throws IOException {
        log.info("=== DOWNLOAD REQUEST START === Material ID: {}", materialID);
        Optional<Material> materialOpt = materialService.getMaterialByID(materialID);

        if (materialOpt.isEmpty()) {
            log.error("Material not found in database: {}", materialID);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Material material = materialOpt.get();
        log.info("Material found: {}, type: {}, path: {}", material.getTitle(), material.getMaterialType(), material.getFilePath());


        if (material.getMaterialType() == Material.MaterialType.VIDEO_LINK) {
            resp.sendRedirect(material.getFilePath());
            return;
        }

        String realPath = req.getServletContext().getRealPath("/");
        log.info("Servlet real path: {}", realPath);


        File file = new File(realPath, material.getFilePath());
        log.info("Looking for file at: {}", file.getAbsolutePath());
        log.info("File exists: {}, Can read: {}, Size: {}", file.exists(), file.canRead(), file.length());


        if (!file.exists()) {
            log.error("FILE NOT FOUND at path: {}", file.getAbsolutePath());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        log.info("Starting file download...");
        materialService.incrementDownloadCount(materialID);

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        resp.setContentLengthLong(file.length());

        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()
        ) {
            byte[] buffer = new byte[4096];
            int r;
            long totalBytes = 0;
            while ((r = in.read(buffer)) != -1) {
                out.write(buffer, 0, r);
                totalBytes += r;
            }
            out.flush();
            log.info("File download completed: {} ({} bytes transferred)", material.getTitle(), totalBytes);

        } catch (IOException e) {
            log.error("Error during file download", e);
            throw e;
        }
        log.info("=== DOWNLOAD REQUEST END ===");
    }
}
