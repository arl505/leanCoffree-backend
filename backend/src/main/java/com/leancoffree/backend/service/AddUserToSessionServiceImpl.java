package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.entity.UsersEntity.UsersId;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
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

  public AddUserToSessionServiceImpl(final UsersRepository usersRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.usersRepository = usersRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  public SuccessOrFailureAndErrorBody addUserToSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest) {

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
        final String websocketMessageString = new JSONObject()
            .put("displayNames", new JSONArray(displayNames)).toString();
        webSocketMessagingTemplate
            .convertAndSend("/topic/users/session/" + refreshUsersRequest.getSessionId(),
                websocketMessageString);
        return new SuccessOrFailureAndErrorBody(SUCCESS, null);
      } else {
        return new SuccessOrFailureAndErrorBody(FAILURE, "How'd that happen? Please try again");
      }

    } else {
      return new SuccessOrFailureAndErrorBody(FAILURE, "Display name already in use in session");
    }
  }

}
