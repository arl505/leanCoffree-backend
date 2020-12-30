package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.RefreshTopicsCommand.FINISH;
import static com.leancoffree.backend.enums.RefreshTopicsCommand.NEXT;
import static com.leancoffree.backend.enums.RefreshTopicsCommand.REVERT_TO_DISCUSSION;
import static com.leancoffree.backend.enums.SortTopicsBy.Y_INDEX;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;
import static com.leancoffree.backend.enums.TopicStatus.DISCUSSED;
import static com.leancoffree.backend.enums.TopicStatus.DISCUSSING;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.model.RefreshTopicsRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.DiscussionVotesRepository;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.TopicsRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import javax.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshTopicsServiceImpl implements RefreshTopicsService {

  @Value("${defaultTopicTime:180}")
  private Integer defaultTopicTime;

  private final TopicsRepository topicsRepository;
  private final BroadcastTopicsService broadcastTopicsService;
  private final SessionsRepository sessionsRepository;
  private final DiscussionVotesRepository discussionVotesRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public RefreshTopicsServiceImpl(final TopicsRepository topicsRepository,
      final BroadcastTopicsService broadcastTopicsService,
      final SessionsRepository sessionsRepository,
      final DiscussionVotesRepository discussionVotesRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.topicsRepository = topicsRepository;
    this.broadcastTopicsService = broadcastTopicsService;
    this.sessionsRepository = sessionsRepository;
    this.discussionVotesRepository = discussionVotesRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  @Transactional
  public SuccessOrFailureAndErrorBody refreshTopics(
      final RefreshTopicsRequest refreshTopicsRequest) {

    final Optional<SessionsEntity> sessionsEntityOptional =
        NEXT.equals(refreshTopicsRequest.getCommand()) || REVERT_TO_DISCUSSION
            .equals(refreshTopicsRequest.getCommand())
            ? sessionsRepository.findById(refreshTopicsRequest.getSessionId())
            : Optional.empty();

    discussionVotesRepository.deleteBySessionId(refreshTopicsRequest.getSessionId());
    final JSONObject messageJson = new JSONObject()
        .put("moreTimeVotesCount", 0)
        .put("finishTopicVotesCount", 0);

    webSocketMessagingTemplate
        .convertAndSend("/topic/discussion-votes/session/" + refreshTopicsRequest.getSessionId(),
            messageJson.toString());

    if (NEXT.equals(refreshTopicsRequest.getCommand()) && sessionsEntityOptional.isPresent()) {
      topicsRepository.updateStatusByTextAndSessionIdAndDisplayName(DISCUSSED.toString(),
          refreshTopicsRequest.getCurrentTopicText(), refreshTopicsRequest.getSessionId(),
          refreshTopicsRequest.getCurrentTopicAuthorDisplayName(), Timestamp.from(Instant.now()));

      topicsRepository.updateStatusByTextAndSessionIdAndDisplayName(DISCUSSING.toString(),
          refreshTopicsRequest.getNextTopicText(), refreshTopicsRequest.getSessionId(),
          refreshTopicsRequest.getNextTopicAuthorDisplayName(), null);

      final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
      sessionsEntity.setCurrentTopicEndTime(Instant.now().plusSeconds(defaultTopicTime));
      sessionsRepository.save(sessionsEntity);

      broadcastTopicsService.broadcastTopics(refreshTopicsRequest.getSessionId(), Y_INDEX, false);
      return new SuccessOrFailureAndErrorBody(SUCCESS, null);
    } else if (FINISH.equals(refreshTopicsRequest.getCommand())) {
      topicsRepository.updateStatusByTextAndSessionIdAndDisplayName(DISCUSSED.toString(),
          refreshTopicsRequest.getCurrentTopicText(), refreshTopicsRequest.getSessionId(),
          refreshTopicsRequest.getCurrentTopicAuthorDisplayName(), Timestamp.from(Instant.now()));
      broadcastTopicsService.broadcastTopics(refreshTopicsRequest.getSessionId(), Y_INDEX, false);
      return new SuccessOrFailureAndErrorBody(SUCCESS, null);
    } else if (REVERT_TO_DISCUSSION.equals(refreshTopicsRequest.getCommand()) && sessionsEntityOptional.isPresent()) {
      topicsRepository.updateStatusByTextAndSessionIdAndDisplayName(DISCUSSING.toString(),
          refreshTopicsRequest.getNextTopicText(), refreshTopicsRequest.getSessionId(),
          refreshTopicsRequest.getNextTopicAuthorDisplayName(), null);

      final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
      sessionsEntity.setCurrentTopicEndTime(Instant.now().plusSeconds(defaultTopicTime));
      sessionsRepository.save(sessionsEntity);

      broadcastTopicsService.broadcastTopics(refreshTopicsRequest.getSessionId(), Y_INDEX, false);
      return new SuccessOrFailureAndErrorBody(SUCCESS, null);
    }
    return new SuccessOrFailureAndErrorBody(FAILURE, "Command or topic/session invalid");
  }
}
