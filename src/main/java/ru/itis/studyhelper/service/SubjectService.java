package ru.itis.studyhelper.service;

import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.Subject;
import ru.itis.studyhelper.dao.SubjectDao;

import java.util.List;
import java.util.Optional;

@Slf4j
public class SubjectService {

    private final SubjectDao subjectDao;

    public SubjectService(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
    }

    public Subject createSubject(String name, String description, String faculty,
                                 Integer semester, Long createByUserID) {
        log.info("Creating subject: {}", name);

        validateSubjectName(name);

        Subject subject = Subject.builder()
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .faculty(faculty != null ? faculty.trim() : null)
                .semester(semester)
                .createdByUserID(createByUserID)
                .build();

        Long id = subjectDao.save(subject);
        log.info("Subject created with id : {}", id);

        return subjectDao.findByID(id).orElseThrow(
                () -> new RuntimeException("")
        );
    }

    public Optional<Subject> getSubjectByID(Long id) {
        return subjectDao.findByID(id);
    }

    public List<Subject> getAllSubjects() {
        return subjectDao.findAll();
    }

    public List<Subject> getSubjectsByFaculty(String faculty) {
        return subjectDao.findByFaculty(faculty);
    }

    public List<Subject> getSubjectsBySemester(Integer semester) {
        return subjectDao.findBySemester(semester);
    }

    public List<Subject> searchSubjects(String query) {
        if (query == null || query.isBlank()) {
            return getAllSubjects();
        }

        return subjectDao.searchByName(query);
    }

    public void updateSubject(Subject subject) {
        validateSubjectName(subject.getName());

        subjectDao.update(subject);
    }

    public void deleteSubject(Long id) {
        subjectDao.delete(id);
    }

    private void validateSubjectName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название предмета не должно быть пустым");
        }

        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Название предмета слишком короткое");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException("Название предмета слишком длинное");
        }
    }
}
