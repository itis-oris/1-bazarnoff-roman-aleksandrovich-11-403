package ru.itis.studyhelper.dao;

import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Response;
import ru.itis.studyhelper.utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ResponseDao implements CrudDao<Response> {
    @Override
    public Long save(Response response) {
        String sql = """
                INSERT INTO responses (help_request_id, responder_id, message, is_accepted)
                VALUES (?, ?, ?, ?)
                RETURNING id;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, response.getHelpRequestID());
            ps.setLong(2, response.getResponderID());
            ps.setString(3, response.getMessage());
            ps.setBoolean(4, response.getIsAccepted());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong("id");
                log.info("Response created with id: {}", id);
                return id;
            }

        } catch (SQLException e) {
            log.error("Error saving response", e);
            throw new RuntimeException();
        }
        return null;
    }

    @Override
    public Optional<Response> findByID(Long id) {
        String sql = """
                SELECT r.id, r.help_request_id, r.responder_id,
                 r.message, r.created_at, r.is_accepted,
                 u.full_name AS responder_name
                FROM responses r
                JOIN users u ON r.responder_id = u.id
                WHERE r.id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToResponse(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding response by id: {}", id, e);
        }

        return Optional.empty();
    }


    public List<Response> findByHelpRequestID(Long helpRequestID) {
        String sql = """
                SELECT r.id, r.help_request_id, r.responder_id,
                 r.message, r.created_at, r.is_accepted,
                 u.full_name AS responder_name
                FROM responses r
                JOIN users u ON r.responder_id = u.id
                WHERE r.help_request_id = ?
                ORDER BY r.created_at ASC
                """;

        List<Response> responses = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, helpRequestID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                responses.add(mapResultSetToResponse(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding responses by help request: {}", helpRequestID, e);
        }

        return responses;
    }

    public List<Response> findByResponderID(Long responderID) {
        String sql = """
                SELECT r.id, r.help_request_id, r.responder_id,
                 r.message, r.created_at, r.is_accepted,
                 u.full_name AS responder_name
                FROM responses r
                JOIN users u ON r.responder_id = u.id
                WHERE r.responder_id = ?
                ORDER BY r.created_at DESC 
                """;

        List<Response> responses = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, responderID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                responses.add(mapResultSetToResponse(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding responses by responder: {}", responderID, e);
        }
        return responses;
    }

    @Override
    public List<Response> findAll() {
        
        
        String sql = """
                SELECT r.id, r.help_request_id, r.responder_id,
                 r.message, r.created_at, r.is_accepted,
                 u.full_name AS responder_name
                FROM responses r
                JOIN users u ON r.responder_id = u.id
                ORDER BY r.created_at ASC
                """;

        List<Response> responses = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {

            while (rs.next()) {
                responses.add(mapResultSetToResponse(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding all responses", e);
        }

        return responses;
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM responses
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setLong(1, id);
            int deleted = ps.executeUpdate();
            log.info("Deleted {} response(s) with id: {}", deleted, id);

        } catch (SQLException e) {
            log.error("Error deleting response: {}", id, e);
            throw new RuntimeException("Failed to delete response", e);
        }
    }

    @Override
    public void update(Response response) {
        String sql = """
                UPDATE responses
                SET message = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setString(1, response.getMessage());
            ps.setLong(2, response.getId());

            int update = ps.executeUpdate();

            log.info("Updated {} response(s) with id: {}", update, response.getId());

        } catch (SQLException e) {
            log.error("Error updating response: {}", response.getId(), e);
            throw new RuntimeException("Failed to update response", e);
        }
    }

    public void acceptResponse(Long id) {
        String sql = """
                UPDATE responses
                SET is_accepted = true
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, id);
            int updated = ps.executeUpdate();
            log.info("Accepted response: {}", id);

        } catch (SQLException e) {
            log.error("Error accepting response: {}", id, e);
            throw new RuntimeException("Failed to accept response", e);
        }
    }

    public Optional<Response> findAcceptedByHelpRequestId(Long helpRequestId) {
        String sql = """
                SELECT r.id, r.help_request_id, r.responder_id,
                 r.message, r.created_at, r.is_accepted,
                 u.full_name AS responder_name
                FROM responses r
                JOIN users u ON r.responder_id = u.id
                WHERE r.help_request_id = ? and r.is_accepted = true
                """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)
        ) {

            ps.setLong(1, helpRequestId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToResponse(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding accepted response for help request: {}", helpRequestId, e);
        }

        return Optional.empty();
    }

    private Response mapResultSetToResponse(ResultSet rs) throws SQLException {
        return Response.builder()
                .id(rs.getLong("id"))
                .helpRequestID(rs.getLong("help_request_id"))
                .responderID(rs.getLong("responder_id"))
                .message(rs.getString("message"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .isAccepted(rs.getBoolean("is_accepted"))
                .responderName(rs.getString("responder_name"))
                .build();
    }
}
