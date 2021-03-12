package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SessionStatus.DISCUSSING;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SessionStatusResponse;
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
        final List<String> moderatorNames = new ArrayList<>();

        for (final UsersEntity user : optionalUsersEntityList.get()) {
          displayNames.add(user.getDisplayName());
          if (user.getIsModerator()) {
            moderatorNames.add(user.getDisplayName());
          }
        }

        if (moderatorNames.isEmpty()) {
          final List<Object[]> alphabeticallyFirstOnlineUser = usersRepository
              .getTopAlphabeticalOnlineUser(usersEntity.getSessionId());

          if (alphabeticallyFirstOnlineUser != null && alphabeticallyFirstOnlineUser.size() == 1) {
            moderatorNames.add((String) alphabeticallyFirstOnlineUser.get(0)[0]);
            final UsersEntity newModeratorEntity = UsersEntity.builder()
                .isOnline(true)
                .displayName((String) alphabeticallyFirstOnlineUser.get(0)[0])
                .isModerator(true)
                .sessionId(usersEntity.getSessionId())
                .websocketUserId((String) alphabeticallyFirstOnlineUser.get(0)[1])
                .build();
            usersRepository.save(newModeratorEntity);

          } else {
            // no one online, return dummy response
            return SessionStatusResponse.builder()
                .status(SUCCESS)
                .error(null)
                .sessionStatus(DISCUSSING)
                .build();
          }
        }

        final String websocketMessageString = new JSONObject()
            .put("displayNames", new JSONArray(displayNames))
            .put("moderator", new JSONArray(moderatorNames)).toString();
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
