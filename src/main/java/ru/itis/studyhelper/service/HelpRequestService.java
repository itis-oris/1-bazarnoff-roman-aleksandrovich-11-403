package ru.itis.studyhelper.service;


import lombok.extern.slf4j.Slf4j;
import ru.itis.studyhelper.models.HelpRequest;
import ru.itis.studyhelper.models.Response;
import ru.itis.studyhelper.dao.HelpRequestDao;
import ru.itis.studyhelper.dao.ResponseDao;
import ru.itis.studyhelper.dao.UserDao;

import java.util.List;
import java.util.Optional;

@Slf4j
public class HelpRequestService {

    private final HelpRequestDao helpRequestDao;
    private final ResponseDao responseDao;
    private final UserDao userDao;

    public HelpRequestService(HelpRequestDao helpRequestDao,
                              ResponseDao responseDao,
                              UserDao userDao
                                 ) {
        this.helpRequestDao = helpRequestDao;
        this.responseDao = responseDao;
        this.userDao = userDao;
    }

    public HelpRequest createHelpRequest(Long authorID, Long subjectID, String title,
                                         String description) {
        log.info("Creating help request by user: {}", authorID);

        validateTitle(title);
        validateDescription(description);

        HelpRequest helpRequest = HelpRequest.builder()
                .authorID(authorID)
                .subjectID(subjectID)
                .title(title)
                .description(description)
                .status(HelpRequest.RequestStatus.OPEN)
                .build();

        Long id = helpRequestDao.save(helpRequest);
        log.info("Help request created with id: {}", id);

        return helpRequestDao.findByID(id).orElseThrow(
                () -> new RuntimeException("Failed to retrieve saved help request")
        );
    }

    public Optional<HelpRequest> getHelpRequestByID(Long id) {
        return helpRequestDao.findByID(id);
    }

    public List<HelpRequest> getAllHelpRequests() {
        log.debug("Getting all help requests");
        return helpRequestDao.findAll();
    }

    public List<HelpRequest> getHelpRequestsByStatus(HelpRequest.RequestStatus status) {
        return helpRequestDao.findByStatus(status);
    }

    public List<HelpRequest> getHelpRequestsBySubject(Long subjectID) {
        return helpRequestDao.findBySubjectID(subjectID);
    }

    public List<HelpRequest> getHelpRequestByAuthor(Long authorID) {
        return helpRequestDao.findByAuthorID(authorID);
    }

    public List<HelpRequest> searchHelpRequest(String query) {
        if (query == null || query.isBlank()) {
            return getAllHelpRequests();
        }
        return helpRequestDao.searchByTitle(query);
    }

    public void updateHelpRequest(HelpRequest helpRequest) {
        validateTitle(helpRequest.getTitle());
        validateDescription(helpRequest.getDescription());

        helpRequestDao.update(helpRequest);
    }

    public void deleteHelpRequest(Long id) {
        helpRequestDao.delete(id);
    }

    public Response addResponse(Long helpRequestID, Long responderID, String message) {
        HelpRequest helpRequest = helpRequestDao.findByID(helpRequestID).orElseThrow(
                () -> new IllegalArgumentException("Запрос помощи не найден")
        );

        if (helpRequest.getStatus() == HelpRequest.RequestStatus.CLOSED) {
            throw new IllegalArgumentException("Нельзя отвечать на закрытый запрос");
        }

        if (helpRequest.getAuthorID().equals(responderID)) {
            throw new IllegalArgumentException("Нельзя отвечать на собственный запрос");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Ответ не может быть пустым");
        }
        if (message.length() < 10) {
            throw new IllegalArgumentException("Ответ слишком короткий (минимум 10 символов)");
        }


        Response response = Response.builder()
                .helpRequestID(helpRequestID)
                .responderID(responderID)
                .message(message)
                .isAccepted(false)
                .build();

        Long id = responseDao.save(response);

        if (helpRequest.getStatus() == HelpRequest.RequestStatus.OPEN) {
            helpRequestDao.updateStatus(helpRequestID, HelpRequest.RequestStatus.IN_PROGRESS);
        }

        return responseDao.findByID(id).orElseThrow(
                () -> new RuntimeException("Failed to retrieve saved response")
        );
    }


    public List<Response> getResponsesForRequest(Long helpRequestID) {
        return responseDao.findByHelpRequestID(helpRequestID);
    }

    public void acceptResponse(Long helpRequestID, Long responseID, Long authorID) {
        HelpRequest helpRequest = helpRequestDao.findByID(helpRequestID).orElseThrow(
                () -> new IllegalArgumentException("Запрос помощи не найден")
        );

        if (!helpRequest.getAuthorID().equals(authorID)) {
            throw new IllegalArgumentException("Только автор запроса может принять ответ");
        }

        if (helpRequest.getStatus() == HelpRequest.RequestStatus.CLOSED) {
            throw new IllegalArgumentException("Запрос уже закрыт");
        }

        Response response = responseDao.findByID(responseID).orElseThrow(
                () -> new IllegalArgumentException("Ответ не найден")
        );

        responseDao.acceptResponse(responseID);
        helpRequestDao.updateStatus(helpRequestID, HelpRequest.RequestStatus.CLOSED);

        userDao.addReputationPoints(response.getResponderID(), 20);

    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Заголовок не может быть пустым");
        }
        if (title.trim().length() < 5) {
            throw new IllegalArgumentException("Заголовок слишком короткий (минимум 5 символов)");
        }
        if (title.trim().length() > 500) {
            throw new IllegalArgumentException("Заголовок слишком длинный (максимум 500 символов)");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }

        if (description.trim().length() < 20) {
            throw new IllegalArgumentException("Описание слишком короткое (минимум 20 символов)");
        }
    }
}
