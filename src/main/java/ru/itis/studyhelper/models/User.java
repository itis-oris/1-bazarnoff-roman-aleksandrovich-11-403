package ru.itis.studyhelper.models;


import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class    User {
    private Long id;
    private String email;
    private String passwordHash;
    private String fullName;
    private String university;
    private String faculty;
    private Integer course;
    private Integer reputationPoints;
    private LocalDateTime registrationDate;
    private String about;
    private UserRole role;


    public enum UserRole {
        MODERATOR,
        USER
    }
}
