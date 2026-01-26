package ru.itis.studyhelper.models;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
    private Long id;
    private String name;
    private String description;
    private String faculty;
    private Integer semester;
    private Long createdByUserID;
    private LocalDateTime createdAt;
}
