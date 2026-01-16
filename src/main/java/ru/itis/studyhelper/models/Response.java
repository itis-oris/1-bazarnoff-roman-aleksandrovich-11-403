package ru.itis.studyhelper.models;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Long id;
    private Long helpRequestID;
    private Long responderID;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isAccepted;


    private String responderName;

}
