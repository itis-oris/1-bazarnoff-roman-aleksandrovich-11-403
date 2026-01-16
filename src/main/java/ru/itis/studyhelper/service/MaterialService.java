package ru.itis.studyhelper.service;

import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Material;
import ru.itis.studyhelper.dao.MaterialDao;

import java.util.List;
import java.util.Optional;


@Slf4j
public class MaterialService {
    private final MaterialDao materialDao;

    public MaterialService(MaterialDao materialDao) {
        this.materialDao = materialDao;
    }

    public Material uploadMaterial(Long authorID, Long subjectID, String title,
                                   String description, Material.MaterialType type,
                                   String filePath
                                   ) {

        validateTitle(title);

        Material material = Material.builder()
                .authorID(authorID)
                .subjectID(subjectID)
                .title(title)
                .description(description != null ? description.trim() : null)
                .materialType(type)
                .filePath(filePath)
                .downloadsCount(0)
                .build();

        Long id = materialDao.save(material);
        log.info("Material uploaded with id: {}", id);

        return materialDao.findByID(id).orElseThrow(
                () -> new RuntimeException("Failed to retrieve saved material")
        );
    }

    public Optional<Material> getMaterialByID(Long id) {
        return materialDao.findByID(id);
    }

    public List<Material> getAllMaterials() {
        return materialDao.findAll();
    }

    public List<Material> getMaterialsBySubject(Long subjectID) {
        return materialDao.findBySubjectID(subjectID);
    }

    public List<Material> getMaterialsByAuthor(Long authorID) {
        return materialDao.findByAuthorID(authorID);
    }

    public List<Material> searchMaterials(String query) {
        if (query == null || query.isBlank()) {
            return getAllMaterials();
        }

        return materialDao.searchByTitle(query);
    }

    public void incrementDownloadCount(Long id) {
        materialDao.incrementDownloadCount(id);
    }

    public void deleteMaterial(Long id) {
        log.info("Deleting material with id: {}", id);
        materialDao.delete(id);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }

        if (title.trim().length() < 3) {
            throw new IllegalArgumentException("Название слишком короткое (минимум 3 символа)");
        }
    }

}
