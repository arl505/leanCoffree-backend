package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.repository.UsersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.json.JSONArray;
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

  @Transactional
  public void dropUserInSessionAndReturnAllUsers(final RefreshUsersRequest refreshUsersRequest) {
    final List<String> displayNames = new ArrayList<>();
    if (isRequestValid(refreshUsersRequest)) {
      final Optional<UsersEntity> optionalUsersEntity = usersRepository
          .findByWebsocketUserId(refreshUsersRequest.getWebsocketUserId());

      if (optionalUsersEntity.isPresent()) {
        usersRepository.delete(optionalUsersEntity.get());

        final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
            .findAllBySessionId(optionalUsersEntity.get().getSessionId());

        if (optionalUsersEntityList.isPresent()) {
          for (final UsersEntity usersEntity : optionalUsersEntityList.get()) {
            displayNames.add(usersEntity.getDisplayName());
          }
          final String websocketMessageString = new JSONObject()
              .put("displayNames", new JSONArray(displayNames)).toString();
          webSocketMessagingTemplate
              .convertAndSend("/topic/session/" + optionalUsersEntity.get().getSessionId(),
                  websocketMessageString);
        }
      }
    }
  }

  private boolean isRequestValid(final RefreshUsersRequest refreshUsersRequest) {
    return refreshUsersRequest.getWebsocketUserId() != null && !refreshUsersRequest
        .getWebsocketUserId().isBlank();
  }

}
