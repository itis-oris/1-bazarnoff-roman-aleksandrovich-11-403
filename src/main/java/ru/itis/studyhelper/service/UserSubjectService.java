package ru.itis.studyhelper.service;

import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Subject;
import ru.itis.studyhelper.models.UserSubject;
import ru.itis.studyhelper.dao.SubjectDao;
import ru.itis.studyhelper.dao.UserSubjectDao;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UserSubjectService {
    private final UserSubjectDao userSubjectDao;
    private final SubjectDao subjectDao;


    public UserSubjectService(UserSubjectDao userSubjectDao, SubjectDao subjectDao) {
        this.userSubjectDao = userSubjectDao;
        this.subjectDao = subjectDao;
    }

    public void addSubjectToUser(Long userId, Long subjectId,
                                 UserSubject.ProficiencyLevel proficiencyLevel,
                                 boolean canHelp) {
        log.info("Adding subject {} to user {} with level {}", subjectId, userId, proficiencyLevel);

        // Проверяем, что предмет существует
        Optional<Subject> subjectOpt = subjectDao.findByID(subjectId);
        if (subjectOpt.isEmpty()) {
            throw new IllegalArgumentException("Предмет не найден");
        }

        // Валидация: нельзя помогать на уровне LEARNING
        if (canHelp && proficiencyLevel == UserSubject.ProficiencyLevel.LEARNING) {
            throw new IllegalArgumentException("Нельзя помогать другим на уровне 'Изучаю'");
        }

        UserSubject userSubject = UserSubject.builder()
                .userId(userId)
                .subjectId(subjectId)
                .proficiencyLevel(proficiencyLevel)
                .canHelp(canHelp)
                .build();

        userSubjectDao.addSubjectToUser(userSubject);
        log.info("Subject {} successfully added to user {}", subjectId, userId);
    }

    public void updateUserSubject(Long userId, Long subjectId,
                                  UserSubject.ProficiencyLevel proficiencyLevel,
                                  boolean canHelp) {
        log.info("Updating subject {} for user {}", subjectId, userId);

        if (!userSubjectDao.userHasSubject(userId, subjectId)) {
            throw new IllegalArgumentException("Пользователь не изучает этот предмет");
        }

        if (canHelp && proficiencyLevel == UserSubject.ProficiencyLevel.LEARNING) {
            throw new IllegalArgumentException("Нельзя помогать другим на уровне 'Изучаю'");
        }

        UserSubject userSubject = UserSubject.builder()
                .userId(userId)
                .subjectId(subjectId)
                .proficiencyLevel(proficiencyLevel)
                .canHelp(canHelp)
                .build();

        userSubjectDao.addSubjectToUser(userSubject);
    }

    public void removeSubjectFromUser(Long userId, Long subjectId) {
        log.info("Removing subject {} from user {}", subjectId, userId);

        if (!userSubjectDao.userHasSubject(userId, subjectId)) {
            throw new IllegalArgumentException("Пользователь не изучает этот предмет");
        }

        userSubjectDao.removeSubjectFromUser(userId, subjectId);
        log.info("Subject {} removed from user {}", subjectId, userId);
    }

    public List<UserSubjectDao.SubjectWithProficiency> getUserSubjectsWithDetails(Long userId) {
        return userSubjectDao.findSubjectsWithDetailsByUserId(userId);
    }

    public List<Subject> getAvailableSubjectsForUser(Long userId) {
        List<Subject> allSubjects = subjectDao.findAll();
        List<UserSubject> userSubjects = userSubjectDao.findByUserId(userId);

        // Фильтруем предметы, которые пользователь уже добавил
        List<Long> userSubjectIds = userSubjects.stream()
                .map(UserSubject::getSubjectId)
                .toList();

        return allSubjects.stream()
                .filter(subject -> !userSubjectIds.contains(subject.getId()))
                .toList();
    }

    public boolean userHasSubject(Long userId, Long subjectId) {
        return userSubjectDao.userHasSubject(userId, subjectId);
    }

    public List<Long> findExpertsBySubject(Long subjectId) {
        return userSubjectDao.findUsersWhoCanHelp(subjectId);
    }

    public int getUserSubjectsCount(Long userId) {
        return userSubjectDao.findByUserId(userId).size();
    }

    public int getUserHelpableSubjectsCount(Long userId) {
        return (int) userSubjectDao.findByUserId(userId).stream()
                .filter(UserSubject::getCanHelp)
                .count();
    }
}
