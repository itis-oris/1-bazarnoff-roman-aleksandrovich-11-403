package ru.itis.studyhelper.models;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSubject {

    private Long userId;
    private Long subjectId;
    private ProficiencyLevel proficiencyLevel;
    private Boolean canHelp;


    public enum ProficiencyLevel {
        LEARNING, KNOWS, EXPERT
    }

}
