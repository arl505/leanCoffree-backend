package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.UsersRepository;
import com.leancoffree.backend.repository.VotesRepository;
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
  public SuccessOrFailureAndErrorBody dropUserInSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest) {
    final List<String> displayNames = new ArrayList<>();
    final Optional<UsersEntity> optionalUsersEntity = usersRepository
        .findByWebsocketUserId(refreshUsersRequest.getWebsocketUserId());

    if (optionalUsersEntity.isPresent()) {
      final UsersEntity usersEntity = optionalUsersEntity.get();
      usersEntity.setIsOnline(false);
      usersRepository.save(usersEntity);

      final Optional<List<UsersEntity>> optionalUsersEntityList = usersRepository
          .findBySessionIdAndIsOnlineTrue(usersEntity.getSessionId());

      if (optionalUsersEntityList.isPresent()) {
        for (final UsersEntity user : optionalUsersEntityList.get()) {
          displayNames.add(user.getDisplayName());
        }
        final String websocketMessageString = new JSONObject()
            .put("displayNames", new JSONArray(displayNames)).toString();
        webSocketMessagingTemplate
            .convertAndSend("/topic/users/session/" + usersEntity.getSessionId(),
                websocketMessageString);
        return new SuccessOrFailureAndErrorBody(SUCCESS, null);
      } else {
        return new SuccessOrFailureAndErrorBody(FAILURE, "How'd that happen? Please try again");
      }

    } else {
      return new SuccessOrFailureAndErrorBody(FAILURE,
          "Username not in use in session, nothing to drop");
    }
  }
}
