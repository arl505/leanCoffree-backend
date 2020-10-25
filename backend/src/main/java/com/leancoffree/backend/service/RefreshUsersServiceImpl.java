package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.RefreshUsersCommand.ADD;
import static com.leancoffree.backend.enums.SessionStatus.DISCUSSING;
import static com.leancoffree.backend.enums.SortTopicsBy.CREATION;
import static com.leancoffree.backend.enums.SortTopicsBy.VOTES;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SessionStatusResponse;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
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
public class RefreshUsersServiceImpl implements RefreshUsersService {

  private final AddUserToSessionService addUserToSessionService;
  private final DropUserInSessionService dropUserInSessionService;
  private final BroadcastTopicsService broadcastTopicsService;
  private final SimpMessagingTemplate webSocketMessagingTemplate;
  private final SessionsRepository sessionsRepository;
  private final UsersRepository usersRepository;

  public RefreshUsersServiceImpl(final AddUserToSessionService addUserToSessionService,
      final DropUserInSessionService dropUserInSessionService,
      final BroadcastTopicsService broadcastTopicsService,
      final SimpMessagingTemplate webSocketMessagingTemplate,
      final SessionsRepository sessionsRepository,
      final UsersRepository usersRepository) {
    this.addUserToSessionService = addUserToSessionService;
    this.dropUserInSessionService = dropUserInSessionService;
    this.broadcastTopicsService = broadcastTopicsService;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
    this.sessionsRepository = sessionsRepository;
    this.usersRepository = usersRepository;
  }

  public SuccessOrFailureAndErrorBody refreshUsers(final RefreshUsersRequest refreshUsersRequest) {
    if (ADD.equals(refreshUsersRequest.getCommand())) {
      return addUserToSessionService.addUserToSessionAndReturnAllUsers(refreshUsersRequest);
    } else {
      return dropUserInSessionService.dropUserInSessionAndReturnAllUsers(refreshUsersRequest);
    }
  }

  public SuccessOrFailureAndErrorBody quickRefresh(String sessionId) {

    final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
        .findBySessionIdAndIsOnlineTrue(sessionId);

    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository
        .findById(sessionId);

    if (sessionsEntityOptional.isPresent()) {
      final String moderatorName;
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

      final List<String> displayNames = new ArrayList<>();
      if(optionalUsersEntityList.isPresent()) {
        for (final UsersEntity usersEntity : optionalUsersEntityList.get()) {
          displayNames.add(usersEntity.getDisplayName());
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
      return SessionStatusResponse.builder()
          .sessionStatus(sessionsEntityOptional.get().getSessionStatus())
          .status(SUCCESS)
          .build();
    }
    return SessionStatusResponse.builder()
        .status(FAILURE)
        .error("Could not find that session id")
        .build();
  }
}
