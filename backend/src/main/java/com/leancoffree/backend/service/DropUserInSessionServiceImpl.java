package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DropUserInSessionServiceImpl implements DropUserInSessionService {

  private final UsersRepository usersRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public DropUserInSessionServiceImpl(final UsersRepository usersRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.usersRepository = usersRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;

  }

  public void dropUserInSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest) {

    if (isRequestValid(refreshUsersRequest)) {

      usersRepository.deleteByWebsocketUserId(refreshUsersRequest.getWebsocketUserId());

      final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
          .findAllBySessionId(refreshUsersRequest.getSessionId());

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
      }
    }
  }

  private boolean isRequestValid(final RefreshUsersRequest refreshUsersRequest) {
    return refreshUsersRequest != null
        && refreshUsersRequest.getDisplayName() != null
        && refreshUsersRequest.getSessionId() != null && !refreshUsersRequest
        .getDisplayName().isBlank() && !refreshUsersRequest.getSessionId().isBlank();
  }

}
