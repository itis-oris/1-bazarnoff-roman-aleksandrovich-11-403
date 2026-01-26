package ru.itis.studyhelper.dao;

import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Subject;
import ru.itis.studyhelper.models.UserSubject;
import ru.itis.studyhelper.utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserSubjectDao implements CrudDao<UserSubject> {
    @Override
    public Long save(UserSubject userSubject) {
        String sql = """
                INSERT INTO users_subject (user_id, subject_id, proficiency_level, can_help)
                                VALUES (?, ?, ?::VARCHAR, ?)
                                ON CONFLICT (user_id, subject_id)
                                DO UPDATE SET
                                    proficiency_level = EXCLUDED.proficiency_level,
                                    can_help = EXCLUDED.can_help
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userSubject.getUserId());
            ps.setLong(2, userSubject.getSubjectId());
            ps.setString(3, userSubject.getProficiencyLevel().name());
            ps.setBoolean(4, userSubject.getCanHelp());

            ps.executeUpdate();
            log.info("Saved/Updated subject {} for user {}", userSubject.getSubjectId(), userSubject.getUserId());

            return userSubject.getUserId();
        } catch (SQLException e) {
            log.error("Error saving UserSubject", e);
            throw new RuntimeException("Ошибка сохранения", e);
        }
    }

    @Override
    public Optional<UserSubject> findByID(Long userID) {
        String sql = """
                SELECT * FROM users_subject
                WHERE user_id = ?
                LIMIT 1
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUserSubject(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding UserSubject by userId {}", userID, e);
        }
        return Optional.empty();
    }

    @Override
    public List<UserSubject> findAll() {
        String sql = """
                SELECT * FROM users_subject 
                ORDER BY user_id, subject_id
                """;

        List<UserSubject> userSubjects = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                userSubjects.add(mapResultSetToUserSubject(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding all UserSubjects", e);
        }

        return userSubjects;
    }

    @Override
    public void delete(Long userID) {
        String sql = """
                DELETE FROM users_subject 
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userID);
            int deleted = ps.executeUpdate();
            log.info("Deleted {} subjects for user {}", deleted, userID);

        } catch (SQLException e) {
            log.error("Error deleting UserSubjects for user {}", userID, e);
            throw new RuntimeException("Failed to delete UserSubjects", e);
        }
    }

    @Override
    public void update(UserSubject userSubject) {
        save(userSubject);
    }

    public List<UserSubject> findByUserId(Long userId) {
        String sql = """
                SELECT * FROM users_subject 
                WHERE user_id = ?
                ORDER BY subject_id
                """;

        List<UserSubject> userSubjects = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                userSubjects.add(mapResultSetToUserSubject(rs));
            }

        } catch (SQLException e) {
            log.error("Error finding subjects for user {}", userId, e);
        }

        return userSubjects;
    }

    public List<SubjectWithProficiency> findSubjectsWithDetailsByUserId(Long userId) {
        String sql = """
                SELECT us.user_id, us.subject_id, us.proficiency_level, us.can_help,
                       s.name as subject_name, s.description, s.faculty, s.semester
                FROM users_subject us
                JOIN subject s ON us.subject_id = s.id
                WHERE us.user_id = ?
                ORDER BY s.name
                """;

        List<SubjectWithProficiency> results = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserSubject userSubject = UserSubject.builder()
                        .userId(rs.getLong("user_id"))
                        .subjectId(rs.getLong("subject_id"))
                        .proficiencyLevel(UserSubject.ProficiencyLevel.valueOf(rs.getString("proficiency_level")))
                        .canHelp(rs.getBoolean("can_help"))
                        .build();

                Subject subject = Subject.builder()
                        .id(rs.getLong("subject_id"))
                        .name(rs.getString("subject_name"))
                        .description(rs.getString("description"))
                        .faculty(rs.getString("faculty"))
                        .semester((Integer) rs.getObject("semester"))
                        .build();

                results.add(new SubjectWithProficiency(userSubject, subject));
            }

        } catch (SQLException e) {
            log.error("Error finding subjects with details for user {}", userId, e);
        }

        return results;
    }

    public void removeSubjectFromUser(Long userId, Long subjectId) {
        String sql = """
                DELETE FROM users_subject 
                WHERE user_id = ? AND subject_id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, subjectId);

            int deleted = ps.executeUpdate();
            log.info("Removed subject {} from user {} (affected rows: {})", subjectId, userId, deleted);

        } catch (SQLException e) {
            log.error("Error removing subject from user", e);
            throw new RuntimeException("Failed to remove subject from user", e);
        }
    }

    public boolean userHasSubject(Long userId, Long subjectId) {
        String sql = """
                SELECT COUNT(*) FROM users_subject 
                WHERE user_id = ? AND subject_id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, subjectId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            log.error("Error checking if user has subject", e);
        }

        return false;
    }

    public List<Long> findUsersWhoCanHelp(Long subjectId) {
        String sql = """
                SELECT user_id FROM users_subject 
                WHERE subject_id = ? AND can_help = true
                ORDER BY 
                    CASE proficiency_level
                        WHEN 'EXPERT' THEN 1
                        WHEN 'KNOWS' THEN 2
                        WHEN 'LEARNING' THEN 3
                    END
                """;

        List<Long> userIds = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, subjectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                userIds.add(rs.getLong("user_id"));
            }

        } catch (SQLException e) {
            log.error("Error finding users who can help with subject {}", subjectId, e);
        }

        return userIds;
    }

    public void addSubjectToUser(UserSubject userSubject) {
        save(userSubject);
    }

    private UserSubject mapResultSetToUserSubject(ResultSet rs) throws SQLException {
        return UserSubject.builder()
            .userId(rs.getLong("user_id"))
            .subjectId(rs.getLong("subject_id"))
            .proficiencyLevel(UserSubject.ProficiencyLevel.valueOf(rs.getString("proficiency_level")))
            .canHelp(rs.getBoolean("can_help"))
            .build();
    }

    public static class SubjectWithProficiency {
        private final UserSubject userSubject;
        private final Subject subject;

        public SubjectWithProficiency(UserSubject userSubject, Subject subject) {
            this.userSubject = userSubject;
            this.subject = subject;
        }

        public UserSubject getUserSubject() {
            return userSubject;
        }

        public Subject getSubject() {
            return subject;
        }
    }
}
