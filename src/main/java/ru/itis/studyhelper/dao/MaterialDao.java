package ru.itis.studyhelper.dao;

import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Material;
import ru.itis.studyhelper.utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MaterialDao implements CrudDao<Material> {
    @Override
    public Long save(Material material) {
        String sql = """
                INSERT INTO materials (author_id, subject_id, title, 
                description, material_type, file_path)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
        ) {

            ps.setLong(1, material.getAuthorID());
            ps.setLong(2, material.getSubjectID());
            ps.setString(3, material.getTitle());
            ps.setString(4, material.getDescription());
            ps.setString(5, material.getMaterialType() != null ?
                            material.getMaterialType().name() :
                            null
                    );
            ps.setString(6, material.getFilePath());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong("id");
                log.info("Material created with id: ", id);
                rs.close();
                return id;
            }
        } catch (SQLException e) {
            log.error("Error saving material", e);
        }
        return null;
    }

    @Override
    public Optional<Material> findByID(Long id) {

        String sql = """
                SELECT m.id, m.author_id, m.subject_id, m.title, m.description, m.material_type, 
                        m.file_path, m.upload_date, m.downloads_count,
                        u.full_name AS author_name, 
                        s.name AS subject_name
                FROM materials m
                    JOIN users u ON m.author_id = u.id
                    JOIN subject s ON m.subject_id = s.id
                WHERE m.id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1 ,id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMaterial(rs));
            }

            rs.close();

        } catch (SQLException e) {
            log.error("Error finding material by id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Material> findAll() {

        String sql = """
                SELECT m.id, m.author_id, m.subject_id, m.title, m.description, m.material_type, 
                        m.file_path, m.upload_date, m.downloads_count,
                        u.full_name AS author_name, 
                        s.name AS subject_name
                FROM materials m
                    JOIN users u ON m.author_id = u.id
                    JOIN subject s ON m.subject_id = s.id
                ORDER BY m.upload_date DESC
                """;

        List<Material> materials = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {

            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding all materials", e);
        }
        return materials;
    }

    public List<Material> findBySubjectID(Long subjectID) {
        String sql = """
                SELECT m.id, m.author_id, m.subject_id, m.title, m.description, m.material_type, 
                        m.file_path, m.upload_date, m.downloads_count,
                        u.full_name AS author_name, 
                        s.name AS subject_name
                FROM materials m
                    JOIN users u ON m.author_id = u.id
                    JOIN subject s ON m.subject_id = s.id
                WHERE s.id = ?
                ORDER BY m.upload_date DESC 
                """;
        List<Material> materials = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
        ) {

            ps.setLong(1, subjectID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }

            rs.close();

        } catch (SQLException e) {
            log.error("Error finding material(s) by subject id: {}", subjectID, e );
        }

        return materials;
    }

    public List<Material> findByAuthorID(Long authorID) {

        String sql = """
                SELECT m.id, m.author_id, m.subject_id, m.title, m.description, m.material_type, 
                        m.file_path, m.upload_date, m.downloads_count,
                        u.full_name AS author_name, 
                        s.name AS subject_name
                FROM materials m
                    JOIN users u ON m.author_id = u.id
                    JOIN subject s ON m.subject_id = s.id
                WHERE u.id = ?
                ORDER BY m.upload_date DESC;
                """;
        List<Material> materials = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setLong(1, authorID);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }

            rs.close();

        } catch (SQLException e) {
            log.error("Error finding material(s) by author id: {}", authorID, e);
        }
        return materials;
    }

    public void incrementDownloadCount(Long id) {
        String sql = """
                UPDATE materials
                SET downloads_count = downloads_count + 1
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, id);
            ps.executeUpdate();
            log.info("Incrementing download count for material: {}", id);

        } catch (SQLException e) {
            log.error("Error incrementing download count", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM materials WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, id);
            int deleted = ps.executeUpdate();
            log.info("Deleting {} material(s) with id: {}", deleted, id);
        } catch (SQLException e) {
            log.error("Error deleting material: {}", id, e);
        }
    }

    @Override
    public void update(Material material) {
        String sql = """
                UPDATE materials
                SET title = ?, description = ?, material_type = ?, file_path = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, material.getTitle());
            ps.setString(2, material.getDescription());
            ps.setString(3, material.getMaterialType() != null ?
                                material.getMaterialType().name() : null
                    );
            ps.setString(4, material.getFilePath());
            ps.setLong(5, material.getId());

            int updated = ps.executeUpdate();
            log.info("Updated {} material(s) with id: {}", updated, material.getId());

        } catch (SQLException e) {
            log.error("Error updating material with id: {}", material.getId(), e);
        }
    }

    public List<Material> searchByTitle(String query) {
        String sql = """
                SELECT m.id, m.author_id, m.subject_id, m.title, m.description, m.material_type, 
                        m.file_path, m.upload_date, m.downloads_count,
                        u.full_name AS author_name, 
                        s.name AS subject_name
                FROM materials m
                    JOIN users u ON m.author_id = u.id
                    JOIN subject s ON m.subject_id = s.id
                WHERE m.title ILIKE ? OR m.description ILIKE ?
                ORDER BY m.upload_date DESC
                """;

        List<Material> materials = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            String searchPattern = "%" + query + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }

            rs.close();
        } catch (SQLException e) {
            log.error("Error searching materials: {}", query, e);
        }

        return materials;
    }

    private Material mapResultSetToMaterial(ResultSet rs) throws SQLException {
        Material.MaterialBuilder builder = Material.builder()
                .id(rs.getLong("id"))
                .authorID(rs.getLong("author_id"))
                .subjectID(rs.getLong("subject_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .filePath(rs.getString("file_path"))
                .uploadDate(rs.getTimestamp("upload_date").toLocalDateTime())
                .downloadsCount(rs.getInt("downloads_count"))
                .authorName(rs.getString("author_name"))
                .subjectName(rs.getString("subject_name"));

        String typeStr = rs.getString("material_type");
        if (typeStr != null) {
            builder.materialType(Material.MaterialType.valueOf(typeStr));
        }

        return builder.build();
    }
}
