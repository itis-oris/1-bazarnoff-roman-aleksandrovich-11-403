package ru.itis.studyhelper.dao;

import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.HelpRequest;
import ru.itis.studyhelper.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HelpRequestDao implements CrudDao<HelpRequest> {


    @Override
    public Long save(HelpRequest helpRequest) {
        String sql = """
                INSERT INTO help_request (author_id, subject_id, title, description, status)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setLong(1, helpRequest.getAuthorID());
            ps.setLong(2, helpRequest.getSubjectID());
            ps.setString(3, helpRequest.getTitle());
            ps.setString(4, helpRequest.getDescription());
            ps.setString(5, helpRequest.getStatus().name());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong("id");
                rs.close();
                return id;
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM help_request WHERE id = ?;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, id);

            int deleted = ps.executeUpdate();
            log.info("Deleted {} help-requests with id: {}", deleted, id);

        } catch (SQLException e) {
            log.error("Error deleting help-request: {}", id, e);
        }
    }

    @Override
    public void update(HelpRequest helpRequest) {
        String sql = """
                UPDATE help_request
                SET title = ?, description = ?, status = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, helpRequest.getTitle());
            ps.setString(2, helpRequest.getDescription());
            ps.setString(3, helpRequest.getStatus().name());
            ps.setLong(4, helpRequest.getId());

            int updated = ps.executeUpdate();
            log.info("Updated {} help-request with id: {}", updated, helpRequest.getId());

        } catch (SQLException e) {
            log.error("Error updating help-request with id: {}", helpRequest.getId(), e);
        }
    }

    public void updateStatus(Long id, HelpRequest.RequestStatus status) {
        String sql = """
                UPDATE help_request
                SET status = ?, closed_at = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, status.name());

            if (status == HelpRequest.RequestStatus.CLOSED) {
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }

            ps.setLong(3, id);

            int updated = ps.executeUpdate();
            log.info("Updated status for help request {}: {}", id, status);

        } catch (SQLException e) {
            log.error("Error updating help-request status with id: {}", id, e);
            throw new RuntimeException("Failed to update help request status");
        }
    }
    @Override
    public Optional<HelpRequest> findByID(Long id) {
        String sql = """
                SELECT hr.id, hr.author_id, hr.subject_id, hr.title, hr.description,
                        hr.status, hr.created_at, hr.closed_at,
                        u.full_name AS author_name, 
                        s.name AS subject_name   
                FROM help_request hr
                 JOIN users u ON hr.author_id = u.id
                 JOIN subject s ON hr.subject_id = s.id
                WHERE hr.id = ?;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToHelpRequest(rs));
            }

            rs.close();

        } catch (SQLException e) {
            log.error("Error finding help-request with id: {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<HelpRequest> findAll() {

        String sql = """
                SELECT hr.id, hr.author_id, hr.subject_id, hr.title, hr.description,
                        hr.status, hr.created_at, hr.closed_at,
                        u.full_name AS author_name,
                        s.name AS subject_name
                FROM help_request hr
                JOIN users u ON hr.author_id = u.id
                JOIN subject s ON hr.subject_id = s.id
                ORDER BY hr.created_at DESC;
                """;

        List<HelpRequest> helpRequests = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                helpRequests.add(mapResultSetToHelpRequest(rs));
            }
            rs.close();
        } catch (SQLException e) {
            log.error("Error finding help-requests", e);
        }
        return helpRequests;
    }

    public List<HelpRequest> findByStatus(HelpRequest.RequestStatus status) {
        String sql = """
                SELECT hr.id, hr.author_id, hr.subject_id, hr.title, hr.description,
                        hr.status, hr.created_at, hr.closed_at,
                        u.full_name AS author_name,
                        s.name AS subject_name
                FROM help_request hr
                JOIN users u ON hr.author_id = u.id
                JOIN subject s ON hr.subject_id = s.id
                WHERE hr.status = ?
                """;

        List<HelpRequest> helpRequests = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                helpRequests.add(mapResultSetToHelpRequest(rs));
            }

            rs.close();
        } catch (SQLException e) {
            log.error("Error finding help-request by status: {}" ,status, e);
            throw new RuntimeException();
        }

        return helpRequests;
    }

    public List<HelpRequest> findBySubjectID(Long subjectID) {
        String sql = """
                SELECT hr.id, hr.author_id, hr.subject_id, hr.title, hr.description,
                        hr.status, hr.created_at, hr.closed_at,
                        u.full_name AS author_name,
                        s.name AS subject_name
                FROM help_request hr
                JOIN users u ON hr.author_id = u.id
                JOIN subject s ON hr.subject_id = s.id
                WHERE s.id = ?;
                """;

        List<HelpRequest> helpRequests = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setLong(1, subjectID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                helpRequests.add(mapResultSetToHelpRequest(rs));
            }
            rs.close();
        } catch (SQLException e) {
            log.error("Error finding help-request by subject id: {}", subjectID, e);
        }
        return helpRequests;
    }

    public List<HelpRequest> findByAuthorID(Long authorID) {
        String sql = """
                SELECT hr.id, hr.author_id, hr.subject_id, hr.title, hr.description,
                        hr.status, hr.created_at, hr.closed_at,
                        u.full_name AS author_name,
                        s.name AS subject_name
                FROM help_request hr
                JOIN users u ON hr.author_id = u.id
                JOIN subject s ON hr.subject_id = s.id
                WHERE u.id = ?
                """;

        List<HelpRequest> helpRequests = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, authorID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                helpRequests.add(mapResultSetToHelpRequest(rs));
            }
            rs.close();
        } catch (SQLException e) {
            log.error("Error finding help-request by author id: {}", authorID, e);
        }
        return helpRequests;
    }

    public List<HelpRequest> searchByTitle(String query) {
        String sql = """
                 SELECT hr.id, hr.author_id, hr.subject_id, hr.title, hr.description,
                        hr.status, hr.created_at, hr.closed_at,
                        u.full_name AS author_name,
                        s.name AS subject_name
                FROM help_request hr
                JOIN users u ON hr.author_id = u.id
                JOIN subject s ON hr.subject_id = s.id
                WHERE hr.title ILIKE ? OR hr.description ILIKE ?
                ORDER BY hr.created_at;
                """;

        List<HelpRequest> helpRequests = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            String searchPatter = "%" + query + "%";
            ps.setString(1, searchPatter);
            ps.setString(2, searchPatter);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                helpRequests.add(mapResultSetToHelpRequest(rs));
            }
            rs.close();
        } catch (SQLException e) {
            log.error("Error searching help request: {}", query, e);
            throw new RuntimeException();
        }

        return helpRequests;
    }


    private HelpRequest mapResultSetToHelpRequest(ResultSet rs) throws SQLException {
        HelpRequest.HelpRequestBuilder builder = HelpRequest.builder()
                .id(rs.getLong("id"))
                .authorID(rs.getLong("author_id"))
                .subjectID(rs.getLong("subject_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .status(HelpRequest.RequestStatus.valueOf(rs.getString("status")))
                .authorName(rs.getString("author_name"))
                .subjectName(rs.getString("subject_name"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp closedAt = rs.getTimestamp("closed_at");
        if (closedAt != null) {
            builder.closedAt(closedAt.toLocalDateTime());
        }

        return builder.build();
    }
}
