package ru.itis.studyhelper.models;


import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private Long helpRequest;
    private Long reviewerId;
    private Long reviewedUserId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

}
