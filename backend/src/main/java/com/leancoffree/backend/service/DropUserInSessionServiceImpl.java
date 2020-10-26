package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;
import static org.json.JSONObject.NULL;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SessionStatusResponse;
import com.leancoffree.backend.repository.SessionsRepository;
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
  private final SessionsRepository sessionsRepository;

  public DropUserInSessionServiceImpl(final UsersRepository usersRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate,
      final SessionsRepository sessionsRepository) {
    this.usersRepository = usersRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
    this.sessionsRepository = sessionsRepository;
  }

  @Transactional
  public SessionStatusResponse dropUserInSessionAndReturnAllUsers(
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
        String moderatorName = null;
        for (final UsersEntity user : optionalUsersEntityList.get()) {
          displayNames.add(user.getDisplayName());
          if(user.getIsModerator()) {
            moderatorName = user.getDisplayName();
          }
        }
        final String websocketMessageString = new JSONObject()
            .put("displayNames", new JSONArray(displayNames))
            .put("moderator", moderatorName == null ? NULL : moderatorName).toString();
        webSocketMessagingTemplate
            .convertAndSend("/topic/users/session/" + usersEntity.getSessionId(),
                websocketMessageString);

        final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository
            .findById(usersEntity.getSessionId());
        if (sessionsEntityOptional.isPresent()) {
          return SessionStatusResponse.builder()
              .status(SUCCESS)
              .error(null)
              .sessionStatus(sessionsEntityOptional.get().getSessionStatus())
              .build();
        }
      }
      return SessionStatusResponse.builder()
          .status(FAILURE)
          .error("How'd that happen? Please try again")
          .build();
    } else {
      return SessionStatusResponse.builder()
          .status(FAILURE)
          .error("Username not in use in session, nothing to drop")
          .build();
    }
  }
}
