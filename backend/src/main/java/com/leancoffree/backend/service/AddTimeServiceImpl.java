package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SortTopicsBy.Y_INDEX;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.model.AddTimeRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.DiscussionVotesRepository;
import com.leancoffree.backend.repository.SessionsRepository;
import java.time.Instant;
import java.util.Optional;
import javax.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AddTimeServiceImpl implements AddTimeService {

  private final SessionsRepository sessionsRepository;
  private final BroadcastTopicsService broadcastTopicsService;
  private final DiscussionVotesRepository discussionVotesRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public AddTimeServiceImpl(final SessionsRepository sessionsRepository,
      final BroadcastTopicsService broadcastTopicsService,
      final DiscussionVotesRepository discussionVotesRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.sessionsRepository = sessionsRepository;
    this.broadcastTopicsService = broadcastTopicsService;
    this.discussionVotesRepository = discussionVotesRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  @Transactional
  public SuccessOrFailureAndErrorBody addTime(final AddTimeRequest addTimeRequest) {
    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository
        .findById(addTimeRequest.getSessionId());
    if (sessionsEntityOptional.isPresent()) {

      int seconds;
      switch (addTimeRequest.getIncrement()) {
        case S30:
          seconds = 30;
          break;
        case M1:
          seconds = 60;
          break;
        case M3:
          seconds = 180;
          break;
        case M5:
          seconds = 60 * 5;
          break;
        case M10:
          seconds = 60 * 10;
          break;
        case M15:
          seconds = 60 * 15;
          break;
        case M30:
          seconds = 60 * 30;
          break;
        case H1:
          seconds = 60 * 60;
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + addTimeRequest.getIncrement());
      }

      final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
      sessionsEntity.setCurrentTopicEndTime(Instant.now().plusSeconds(seconds));
      sessionsRepository.save(sessionsEntity);

      broadcastTopicsService.broadcastTopics(addTimeRequest.getSessionId(), Y_INDEX, false);

      discussionVotesRepository.deleteBySessionId(addTimeRequest.getSessionId());
      final JSONObject messageJson = new JSONObject()
          .put("moreTimeVotesCount", 0)
          .put("finishTopicVotesCount", 0);

      webSocketMessagingTemplate
          .convertAndSend("/topic/discussion-votes/session/" + addTimeRequest.getSessionId(),
              messageJson.toString());

      return new SuccessOrFailureAndErrorBody(SUCCESS, null);
    }
    return new SuccessOrFailureAndErrorBody(FAILURE, "Couldn't find session");
  }
}
