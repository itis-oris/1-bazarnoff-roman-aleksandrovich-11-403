package ru.itis.studyhelper.utils;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

@Slf4j
public class PasswordUtil {
    private static final int BCRYPT_ROUNDS = 12;


    public static String hashPassword(String password) {
        log.debug("Hashing debug");
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
        log.debug("Password hashed successfully");
        return hashed;
    }

    public static boolean checkPassword(String plainPassword, String hashPassword) {
        log.debug("Checking password");
        try {
            boolean matched = BCrypt.checkpw(plainPassword, hashPassword);
            log.debug("Password check result {}", matched);
            return matched;
        } catch (Exception e) {
            log.debug("Error checking password", e);
            return false;
        }
    }
}
