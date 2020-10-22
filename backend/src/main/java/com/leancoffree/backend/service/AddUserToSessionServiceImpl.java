package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SessionStatus.DISCUSSING;
import static com.leancoffree.backend.enums.SessionStatus.STARTED;
import static com.leancoffree.backend.enums.SortTopicsBy.CREATION;
import static com.leancoffree.backend.enums.SortTopicsBy.VOTES;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SessionStatusResponse;
import com.leancoffree.backend.enums.SessionStatus;
import com.leancoffree.backend.enums.SortTopicsBy;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AddUserToSessionServiceImpl implements AddUserToSessionService {

  private final UsersRepository usersRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;
  private final BroadcastTopicsService broadcastTopicsService;
  private final SessionsRepository sessionsRepository;

  public AddUserToSessionServiceImpl(final UsersRepository usersRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate,
      final BroadcastTopicsService broadcastTopicsService,
      final SessionsRepository sessionsRepository) {
    this.usersRepository = usersRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
    this.broadcastTopicsService = broadcastTopicsService;
    this.sessionsRepository = sessionsRepository;
  }

  public SessionStatusResponse addUserToSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest) {

    final String displayName = refreshUsersRequest.getDisplayName();
    final String sessionId = refreshUsersRequest.getSessionId();

    final UsersId usersId = new UsersId(displayName, sessionId);
    final Optional<UsersEntity> usersEntityOptional = usersRepository.findById(usersId);
    final long usersInSessionCount = usersRepository.countBySessionId(sessionId);

    if (usersEntityOptional.isEmpty() || !usersEntityOptional.get().getIsOnline()) {
      usersRepository.save(UsersEntity.builder()
          .displayName(displayName)
          .sessionId(sessionId)
          .websocketUserId(refreshUsersRequest.getWebsocketUserId())
          .isOnline(true)
          .isModerator(usersInSessionCount == 0)
          .build());

      final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
          .findBySessionIdAndIsOnlineTrue(sessionId);

      final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository
          .findById(refreshUsersRequest.getSessionId());

      if (optionalUsersEntityList.isPresent() && sessionsEntityOptional.isPresent()) {
        final List<String> displayNames = new ArrayList<>();
        for (final UsersEntity usersEntity : optionalUsersEntityList.get()) {
          displayNames.add(usersEntity.getDisplayName());
        }

        final String moderatorName;
        if (usersInSessionCount == 0) {
          moderatorName = displayName;
        } else {
          final Optional<UsersEntity> moderatorUserEntityOptional = usersRepository
              .findBySessionIdAndIsModeratorTrue(sessionId);
          if(moderatorUserEntityOptional.isPresent()) {
            moderatorName = moderatorUserEntityOptional.get().getDisplayName();
          } else {
            return SessionStatusResponse.builder()
                .status(FAILURE)
                .error("Couldn't find moderator")
                .build();
          }
        }

        final String websocketMessageString = new JSONObject()
            .put("displayNames", new JSONArray(displayNames))
            .put("moderator", moderatorName).toString();
        final SortTopicsBy sortTopicsBy =
            sessionsEntityOptional.get().getSessionStatus().equals(DISCUSSING)
                ? VOTES
                : CREATION;

        webSocketMessagingTemplate
            .convertAndSend("/topic/users/session/" + sessionId, websocketMessageString);
        broadcastTopicsService.broadcastTopics(sessionId, sortTopicsBy, false);

        SessionStatus sessionStatus = sessionsEntityOptional.get().getSessionStatus();
        if (usersInSessionCount == 0) {
          final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
          sessionsEntity.setSessionStatus(STARTED);
          sessionsRepository.save(sessionsEntity);
          sessionStatus = STARTED;
        }
        return SessionStatusResponse.builder()
            .status(SUCCESS)
            .error(null)
            .sessionStatus(sessionStatus)
            .build();
      }
      return SessionStatusResponse.builder()
          .status(FAILURE)
          .error("How'd that happen? Please try again")
          .build();
    } else {
      return SessionStatusResponse.builder()
          .status(FAILURE)
          .error("Display name already in use in session")
          .build();
    }
  }
}
