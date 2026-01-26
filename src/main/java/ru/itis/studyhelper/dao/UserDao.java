package ru.itis.studyhelper.dao;

import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.User;
import ru.itis.studyhelper.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserDao implements CrudDao<User> {


    public Long save(User user) {
        String sql = """
                INSERT INTO users (email, password_hash, full_name, university,
                                   faculty, course, about, role)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getUniversity());
            ps.setString(5, user.getFaculty());

            if (user.getCourse() != null) {
                ps.setInt(6, user.getCourse());

            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setString(8, user.getRole() != null ? user.getRole().name() : "USER");

            ps.setString(7, user.getAbout());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong("id");
                log.info("User created with id: {}", id);
                return id;
            }

        } catch (SQLException e) {
            log.info("Error saving user", e);
            throw new RuntimeException("Error saving user", e);
        }
        return null;
    }

    public List<User> findAll() {
        String sql = """
                SELECT * FROM users ORDER BY registration_date DESC;
                """;
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            log.info("ошибка поиска пользователей", e);
        }

        return users;
    }

    public Optional<User> findByID(Long id) {
        String sql = """
                SELECT * FROM users WHERE id = ?;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding user by id {}", id, e);
        }

        return Optional.empty();
    }

    public Optional<User> findUserByEmail(String email) {
        String sql = """
                SELECT * FROM users WHERE email = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding user by email {}", email, e);
        }
        return Optional.empty();
    }

    public void update(User user) {
        String sql = """
                UPDATE  users
                SET full_name = ?, university = ?, faculty = ?,
                    course = ?, about = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getUniversity());
            ps.setString(3, user.getFaculty());
            ps.setInt(4, user.getCourse());
            ps.setString(5, user.getAbout());
            ps.setLong(7, user.getId());


            int updated = ps.executeUpdate();
            log.info("Updated {} user(s) with id: {}", updated, user.getId());

        } catch (SQLException e) {
            log.error("Error updating user: {}", user.getId(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public void delete(Long id) {
        String sql = """
                DELETE FROM users WHERE id = ?;
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            int deleted = ps.executeUpdate();
            log.info("Deleted {} user(s) with id: {}", deleted, id);


        } catch (SQLException e) {
            log.error("Error deleting user: {}", id, e);
        }
    }


    public List<User> findTopByReputation(int limit) {
        String sql = """
                SELECT * FROM users
                ORDER BY reputation_points DESC
                LIMIT ?
                """;

        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }


        } catch (SQLException e) {
            log.error("Error finding top users", e);
        }

        return users;
    }

    public void addReputationPoints(Long userID, int points) {
        String sql = """
                     UPDATE users
                     SET reputation_points = reputation_points + ?
                     WHERE id = ?
                     """;

        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, points);
            ps.setLong(2, userID);

            ps.executeUpdate();

            log.info("Added {} reputation points to user {}", points, userID);
        } catch (SQLException e) {
            log.error("Error adding reputation points", e);
        }

    }


    private User mapResultSetToUser(ResultSet rs) throws SQLException {

        User.UserBuilder builder = User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .passwordHash(rs.getString("password_hash"))
                .fullName(rs.getString("full_name"))
                .university(rs.getString("university"))
                .faculty(rs.getString("faculty"))
                .course(rs.getInt("course"))
                .reputationPoints(rs.getInt("reputation_points"))
                .registrationDate(rs.getTimestamp("registration_date").toLocalDateTime())
                .about(rs.getString("about"));

        String role = rs.getString("role");
        if (role != null) {
            builder.role(User.UserRole.valueOf(role));
        } else {
            builder.role(User.UserRole.USER);
        }

        return builder.build();
    }
}
