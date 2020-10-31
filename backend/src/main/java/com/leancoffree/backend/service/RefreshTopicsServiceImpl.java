package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.RefreshTopicsCommand.FINISH;
import static com.leancoffree.backend.enums.RefreshTopicsCommand.NEXT;
import static com.leancoffree.backend.enums.SortTopicsBy.Y_INDEX;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;
import static com.leancoffree.backend.enums.TopicStatus.DISCUSSED;
import static com.leancoffree.backend.enums.TopicStatus.DISCUSSING;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.model.RefreshTopicsRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.TopicsRepository;
import java.time.Instant;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTopicsServiceImpl implements RefreshTopicsService {

  @Value("${defaultTopicTime}")
  private Integer defaultTopicTime;

  private final TopicsRepository topicsRepository;
  private final BroadcastTopicsService broadcastTopicsService;
  private final SessionsRepository sessionsRepository;

  public RefreshTopicsServiceImpl(final TopicsRepository topicsRepository,
      final BroadcastTopicsService broadcastTopicsService,
      final SessionsRepository sessionsRepository) {
    this.topicsRepository = topicsRepository;
    this.broadcastTopicsService = broadcastTopicsService;
    this.sessionsRepository = sessionsRepository;
  }

  @Transactional
  public SuccessOrFailureAndErrorBody refreshTopics(
      final RefreshTopicsRequest refreshTopicsRequest) {

    if (NEXT.equals(refreshTopicsRequest.getCommand())) {
      final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository
          .findById(refreshTopicsRequest.getSessionId());

      if (sessionsEntityOptional.isPresent()) {
        topicsRepository.updateStatusByTextAndSessionIdAndDisplayName(DISCUSSED.toString(),
            refreshTopicsRequest.getCurrentTopicText(), refreshTopicsRequest.getSessionId(),
            refreshTopicsRequest.getCurrentTopicAuthorDisplayName());

        topicsRepository.updateStatusByTextAndSessionIdAndDisplayName(DISCUSSING.toString(),
            refreshTopicsRequest.getNextTopicText(), refreshTopicsRequest.getSessionId(),
            refreshTopicsRequest.getNextTopicAuthorDisplayName());

        final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
        sessionsEntity.setCurrentTopicEndTime(Instant.now().plusSeconds(defaultTopicTime));
        sessionsRepository.save(sessionsEntity);

        broadcastTopicsService.broadcastTopics(refreshTopicsRequest.getSessionId(), Y_INDEX, false);
        return new SuccessOrFailureAndErrorBody(SUCCESS, null);
      }
    } else if (FINISH.equals(refreshTopicsRequest.getCommand())) {
      topicsRepository.updateStatusByTextAndSessionIdAndDisplayName(DISCUSSED.toString(),
          refreshTopicsRequest.getCurrentTopicText(), refreshTopicsRequest.getSessionId(),
          refreshTopicsRequest.getCurrentTopicAuthorDisplayName());
      broadcastTopicsService.broadcastTopics(refreshTopicsRequest.getSessionId(), Y_INDEX, false);
      return new SuccessOrFailureAndErrorBody(SUCCESS, null);
    }
    return new SuccessOrFailureAndErrorBody(FAILURE, "Command or topic/session invalid");
  }
}
