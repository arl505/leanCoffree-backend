package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SessionStatus.DISCUSSING;
import static com.leancoffree.backend.enums.SortTopicsBy.CREATION;
import static com.leancoffree.backend.enums.SortTopicsBy.Y_INDEX;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import com.leancoffree.backend.domain.model.AddUserResponse;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.enums.SortTopicsBy;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

  public AddUserResponse addUserToSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest) {

    final String displayName = refreshUsersRequest.getDisplayName();
    final String sessionId = refreshUsersRequest.getSessionId();

    final UsersId usersId = new UsersId(displayName, sessionId);

    final Optional<UsersEntity> usersEntityOptional = usersRepository.findById(usersId);
    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository
        .findById(refreshUsersRequest.getSessionId());

    if (usersEntityOptional.isEmpty() || !usersEntityOptional.get().getIsOnline()) {
      final boolean wasAlreadyModerator = wasAlreadyModerator(usersEntityOptional);
      final boolean isModerator =
          usersRepository.countBySessionIdAndIsOnlineTrueAndIsModeratorTrue(sessionId) == 0
              || wasAlreadyModerator;

      usersRepository.save(UsersEntity.builder()
          .displayName(displayName)
          .sessionId(sessionId)
          .websocketUserId(refreshUsersRequest.getWebsocketUserId())
          .isOnline(true)
          .isModerator(isModerator)
          .build());

      final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
          .findBySessionIdAndIsOnlineTrue(sessionId);

      if (optionalUsersEntityList.isPresent() && sessionsEntityOptional.isPresent()) {
        final Map.Entry<List<String>, List<String>> displayNamesAndModeratorName = getDisplayNamesAndModeratorName(
            optionalUsersEntityList.get());

        final String websocketMessageString = new JSONObject()
            .put("displayNames", new JSONArray(displayNamesAndModeratorName.getKey()))
            .put("moderator", new JSONArray(displayNamesAndModeratorName.getValue())).toString();
        final SortTopicsBy sortTopicsBy =
            sessionsEntityOptional.get().getSessionStatus().equals(DISCUSSING)
                ? Y_INDEX
                : CREATION;

        webSocketMessagingTemplate
            .convertAndSend("/topic/users/session/" + sessionId, websocketMessageString);
        broadcastTopicsService.broadcastTopics(sessionId, sortTopicsBy, false);

        return AddUserResponse.builder()
            .showShareableLink(isModerator && !wasAlreadyModerator)
            .status(SUCCESS)
            .error(null)
            .sessionStatus(sessionsEntityOptional.get().getSessionStatus())
            .build();
      }
      return AddUserResponse.builder()
          .showShareableLink(false)
          .status(FAILURE)
          .error("How'd that happen? Please try again")
          .build();
    } else {
      return AddUserResponse.builder()
          .showShareableLink(false)
          .status(FAILURE)
          .error("Display name already in use in session")
          .build();
    }
  }

  private boolean wasAlreadyModerator(final Optional<UsersEntity> usersEntityOptional) {
    boolean wasAlreadyModerator = false;
    if (usersEntityOptional.isPresent()) {
      wasAlreadyModerator = usersEntityOptional.get().getIsModerator();
    }
    return wasAlreadyModerator;
  }

  private Map.Entry<List<String>, List<String>> getDisplayNamesAndModeratorName(
      final List<UsersEntity> optionalUsersEntityList) {
    final List<String> moderators = new ArrayList<>();
    final List<String> displayNames = new ArrayList<>();
    for (final UsersEntity usersEntity : optionalUsersEntityList) {
      displayNames.add(usersEntity.getDisplayName());
      if (usersEntity.getIsModerator()) {
        moderators.add(usersEntity.getDisplayName());
      }
    }
    return Map.entry(displayNames, moderators);
  }
}
