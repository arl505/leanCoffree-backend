package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.DiscussionVoteType.MORE_TIME;
import static com.leancoffree.backend.enums.DiscussionVoteType.FINISH_TOPIC;
import static com.leancoffree.backend.enums.RefreshUsersCommand.ADD;
import static com.leancoffree.backend.enums.SessionStatus.DISCUSSING;
import static com.leancoffree.backend.enums.SortTopicsBy.CREATION;
import static com.leancoffree.backend.enums.SortTopicsBy.Y_INDEX;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.DiscussionVotesEntity;
import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.UsersEntity;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SessionStatusResponse;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.enums.SortTopicsBy;
import com.leancoffree.backend.repository.DiscussionVotesRepository;
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
  private final DiscussionVotesRepository discussionVotesRepository;

  public RefreshUsersServiceImpl(final AddUserToSessionService addUserToSessionService,
      final DropUserInSessionService dropUserInSessionService,
      final BroadcastTopicsService broadcastTopicsService,
      final SimpMessagingTemplate webSocketMessagingTemplate,
      final SessionsRepository sessionsRepository,
      final UsersRepository usersRepository,
      final DiscussionVotesRepository discussionVotesRepository) {
    this.addUserToSessionService = addUserToSessionService;
    this.dropUserInSessionService = dropUserInSessionService;
    this.broadcastTopicsService = broadcastTopicsService;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
    this.sessionsRepository = sessionsRepository;
    this.usersRepository = usersRepository;
    this.discussionVotesRepository = discussionVotesRepository;
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
              ? Y_INDEX
              : CREATION;

      webSocketMessagingTemplate
          .convertAndSend("/topic/users/session/" + sessionId, websocketMessageString);
      broadcastTopicsService.broadcastTopics(sessionId, sortTopicsBy, false);

      final List<DiscussionVotesEntity> nextTopicVotes = discussionVotesRepository
          .findAllBySessionIdAndVoteType(sessionId, FINISH_TOPIC);
      final List<DiscussionVotesEntity> moreTimeVotes = discussionVotesRepository
          .findAllBySessionIdAndVoteType(sessionId, MORE_TIME);

      final JSONObject messageJson = new JSONObject()
          .put("moreTimeVotesCount", moreTimeVotes.size())
          .put("finishTopicVotesCount", nextTopicVotes.size());

      webSocketMessagingTemplate
          .convertAndSend("/topic/discussion-votes/session/" + sessionId, messageJson.toString());

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
