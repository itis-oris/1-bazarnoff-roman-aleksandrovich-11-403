package ru.itis.studyhelper.dao;


import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Subject;
import ru.itis.studyhelper.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
public class SubjectDao implements CrudDao<Subject> {
    public Long save(Subject subject) {
        String sql = """
                INSERT INTO subject (name, description, faculty,
                                     semester, created_by_user_id)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, subject.getName());
            ps.setString(2, subject.getDescription());
            ps.setString(3, subject.getFaculty());

            if (subject.getSemester() != null) {
                ps.setInt(4, subject.getSemester());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (subject.getCreatedByUserID() != null) {
                ps.setLong(5, subject.getCreatedByUserID());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                log.info("Subject created with id: {}", id);
                return id;
            }

        } catch (SQLException e) {
            log.error("Error saving subject", e);
            throw new RuntimeException("Failed to save subject", e);
        }

        return null;
    }

    public Optional<Subject> findByID(Long id) {
        String sql = """
                SELECT * FROM subject WHERE id = ?;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding user by id {}", id, e);
        }

        return Optional.empty();
    }


    public List<Subject> findAll() {
        String sql =
                """
                        SELECT * FROM subject ORDER BY name;
                        """;

        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
        ) {

            while (rs.next()) {
                subjects.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding all subjects", e);
        }

        return subjects;
    }

    public List<Subject> findByFaculty(String faculty) {
        String sql = """
                SELECT * FROM subject WHERE faculty = ? ORDER BY name;
                """;

        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
        ) {

            ps.setString(1, faculty);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                subjects.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding subject by faculty: {}", faculty, e);
        }
        return subjects;
    }

    public List<Subject> findBySemester(Integer semester) {

        String sql = """
                SELECT * FROM subject WHERE semester = ? ORDER BY name;
                """;
        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setInt(1, semester);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                subjects.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding subject by semester: {}", semester, e);
        }

        return subjects;
    }

    public void update(Subject subject) {
        String sql = """
                UPDATE  subject
                SET name = ?, description = ?, faculty = ?,
                semester = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, subject.getName());
            ps.setString(2, subject.getDescription());
            ps.setString(3, subject.getFaculty());

            if (subject.getSemester() != null) {
                ps.setInt(4, subject.getSemester());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setLong(5, subject.getId());
            int updated = ps.executeUpdate();
            log.info("Update {} subject with id: {}", updated, subject.getId());
        } catch (SQLException e) {

            log.error("Error updating subject: {}", subject.getId(), e);

        }
    }

    public void delete(Long id) {
        String sql = """
                DELETE FROM subject WHERE id = ?;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setLong(1, id);
            int deleted = ps.executeUpdate();
            log.info("Deleted {} subject with id: {}", deleted, id);

        } catch (SQLException e) {
            log.error("Error deleting subject: {}", id, e);
        }
    }

    public List<Subject> searchByName(String query) {
        String sql = """
                SELECT * FROM subject WHERE name ILIKE ? ORDER BY name
                """;
        List<Subject> subjects = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, "%" + query + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                subjects.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            log.error("Error searching by name: {}", query, e);
        }

        return subjects;
    }


    private Subject mapResultSetToUser(ResultSet rs) throws SQLException {
        return Subject.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .faculty(rs.getString("faculty"))
                .semester((Integer) rs.getObject("semester"))
                .createdByUserID((Long) rs.getObject("created_by_user_id"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
