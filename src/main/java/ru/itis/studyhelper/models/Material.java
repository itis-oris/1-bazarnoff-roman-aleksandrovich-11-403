package ru.itis.studyhelper.models;


import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Material {


    private Long id;
    private Long authorID;
    private Long subjectID;
    private String title;
    private String description;
    private MaterialType materialType;
    private String filePath;
    private LocalDateTime uploadDate;
    private Integer downloadsCount;


    private String authorName;
    private String subjectName;


    public enum MaterialType {
        NOTES, PRESENTATION, VIDEO_LINK, SUMMARY
    }
}
