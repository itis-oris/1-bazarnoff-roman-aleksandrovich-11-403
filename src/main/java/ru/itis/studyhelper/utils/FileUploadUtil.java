package ru.itis.studyhelper.utils;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
public class FileUploadUtil {
    private static final String UPLOAD_DIR = "uploads";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;


    public static String saveFile(HttpServletRequest request, Part filePart, String subDirectory) throws IOException {
        log.info("=== FILE UPLOAD START ===");
        if (filePart == null || filePart.getSize() == 0) {
            log.error("File part is null or empty");
            throw new IllegalArgumentException("Файл не выбран");
        }

        log.info("File part size: {} bytes", filePart.getSize());

        if (filePart.getSize() > MAX_FILE_SIZE) {
            log.error("File too large: {} bytes", filePart.getSize());
            throw new IllegalArgumentException("Файл слишком большой (максимум 10 MB)");
        }

        String fileName = getFileName(filePart);
        log.info("Original file name: {}", fileName);

        if (fileName == null || fileName.isEmpty()) {
            log.error("File name is null or empty");
            throw new IllegalArgumentException("Некорректное имя файла");
        }

        String extension = getFileExtension(fileName);
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        log.info("Generated unique file name: {}", uniqueFileName);


        String realPath = request.getServletContext().getRealPath("/");
        Path uploadPath = Paths.get(realPath, UPLOAD_DIR, subDirectory);
        log.info("Servlet context real path: {}", realPath);

        if (!Files.exists(uploadPath)) {
            log.info("Upload directory doesn't exist, creating...");
            Files.createDirectories(uploadPath);
            log.info("Upload directory created successfully");
        } else {
            log.info("Upload directory already exists");
        }

        Path filePath = uploadPath.resolve(uniqueFileName);
        log.info("Full file path: {}", filePath.toAbsolutePath());

        try (InputStream input = filePart.getInputStream()) {
            long bytesCopied = Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved successfully! Bytes written: {}", bytesCopied);
        } catch (IOException e) {
            log.error("Error saving file", e);
            throw e;
        }

        if (Files.exists(filePath)) {
            long fileSize = Files.size(filePath);
            log.info("File verification: EXISTS, size: {} bytes", fileSize);
        } else {
            log.error("File verification FAILED: file does not exist after save!");
        }

        return UPLOAD_DIR + "/" + subDirectory + "/" + uniqueFileName;
    }

    public static String saveMaterial(HttpServletRequest request, Part filePart) throws IOException {
        return saveFile(request, filePart, "materials");
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }

        return "";
    }

    private static String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            for (String token  : contentDisposition.split(";")) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                }
            }
        }
        return null;
    }

    private static void deleteFile(String filePath, HttpServletRequest request) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        try {
            String realPath = request.getServletContext().getRealPath("/");
            Path path = Paths.get(realPath, filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Error");
        }
    }
}
