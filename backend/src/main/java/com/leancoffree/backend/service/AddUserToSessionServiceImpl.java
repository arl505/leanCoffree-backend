package com.leancoffree.backend.service;

import com.leancoffree.backend.controller.RefreshUsersInSessionException;
import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AddUserToSessionServiceImpl implements AddUserToSessionService {

  private final UsersRepository usersRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public AddUserToSessionServiceImpl(final UsersRepository usersRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.usersRepository = usersRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  public void addUserToSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest)
      throws RefreshUsersInSessionException {

    if (isRequestValid(refreshUsersRequest)) {
      final String displayName = refreshUsersRequest.getDisplayName();
      final String sessionId = refreshUsersRequest.getSessionId();

      final UsersId usersId = new UsersId(displayName, sessionId);
      final Optional<UsersEntity> usersEntityOptional = usersRepository.findById(usersId);

      if (usersEntityOptional.isEmpty()) {
        usersRepository.save(UsersEntity.builder()
            .displayName(displayName)
            .sessionId(sessionId)
            .votesUsed(0)
            .websocketUserId(refreshUsersRequest.getWebsocketUserId())
            .build());

        final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
            .findAllBySessionId(sessionId);

        if (optionalUsersEntityList.isPresent()) {
          final List<String> displayNames = new ArrayList<>();
          for (final UsersEntity usersEntity : optionalUsersEntityList.get()) {
            displayNames.add(usersEntity.getDisplayName());
          }

          final JSONObject webSocketMessageJson = new JSONObject()
              .put("displayNames", displayNames);
          final String websocketMessageString = webSocketMessageJson.toString();

          webSocketMessagingTemplate
              .convertAndSend("/topic/session/" + refreshUsersRequest.getSessionId(),
                  websocketMessageString);

        } else {
          throw new RefreshUsersInSessionException("How'd that happen? Please try again");
        }

      } else {
        throw new RefreshUsersInSessionException("Display name already in use for session");
      }
    } else {
      throw new RefreshUsersInSessionException("Invalid request");
    }
  }

  private boolean isRequestValid(final RefreshUsersRequest refreshUsersRequest) {
    return refreshUsersRequest != null && refreshUsersRequest.getDisplayName() != null
        && refreshUsersRequest.getSessionId() != null && !refreshUsersRequest.getDisplayName()
        .isBlank() && !refreshUsersRequest.getSessionId().isBlank();
  }

}
