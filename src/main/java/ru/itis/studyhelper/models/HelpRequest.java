package ru.itis.studyhelper.models;


import lombok.*;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HelpRequest {
    private Long id;
    private Long authorID;
    private Long subjectID;
    private String title;
    private String description;
  // private DifficultyLevel difficultyLevel;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;


    private String authorName;
    private String subjectName;

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }

    public enum RequestStatus {
        OPEN, IN_PROGRESS, CLOSED
    }
}
