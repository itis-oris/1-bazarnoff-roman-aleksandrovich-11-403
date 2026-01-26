package ru.itis.studyhelper.service;


import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.User;
import ru.itis.studyhelper.dao.UserDao;
import ru.itis.studyhelper.utils.PasswordUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }


    public User register(String email, String password, String fullname,
                         String university, String faculty, Integer course) {
        log.info("Registration attempt for email: {}", email);

        if (userDao.findUserByEmail(email).isPresent()) {
            log.warn("Registration failed: email already exists: {}", email);
            throw new IllegalArgumentException("Почта уже зарегистрирована");
        }

        log.info("validating email password fullname");
        validateEmail(email);
        validatePassword(password);
        validateFullName(fullname);

        log.info("hashing password");
        String hashedPassword = PasswordUtil.hashPassword(password);


        log.info("building user");
        User user = User.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .fullName(fullname)
                .university(university)
                .faculty(faculty)
                .course(course)
                .reputationPoints(0)
                .role(User.UserRole.USER)
                .registrationDate(LocalDateTime.now())
                .build();


        Long userID = userDao.save(user);
        log.info("User registered successfully: {} with id: {}", email, userID);

        return userDao.findByID(userID).orElseThrow(
                () -> new RuntimeException("Failed to retrieve saved user")
        );
    }


    public User authenticate(String email, String password) {
        Optional<User> userOpt = userDao.findUserByEmail(email);

        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Неверная почта или пароль");
        }

        User user = userOpt.get();

        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new AuthenticationException("Неверная почта или пароль");
        }

        return user;
    }

    public Optional<User> getUserByID(Long id) {
        return userDao.findByID(id);
    }

    public void updateProfile(User user) {
        validateFullName(user.getFullName());

        userDao.update(user);
    }


    public List<User> getTopUsersByPoints(int limit) {
        return userDao.findTopByReputation(limit);
    }

    public void addReputation(Long userID, int points) {
        userDao.addReputationPoints(userID, points);
    }


        private void validateEmail(String email) {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Почта не может быть пустой");
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new IllegalArgumentException("Неверный формат почты");
            }
        }


    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 character");
        }
    }

    private void validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Полное имя не может быть пустым");
        }
        if (fullName.length() < 2) {
            throw new IllegalArgumentException("Полное имя слишком короткое");
        }
    }


    public static class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }

}
