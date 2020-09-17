package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.RefreshTopicsCommand.FINISH;
import static com.leancoffree.backend.enums.RefreshTopicsCommand.NEXT;
import static com.leancoffree.backend.enums.SortTopicsBy.VOTES;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;
import static com.leancoffree.backend.enums.TopicStatus.DISCUSSED;
import static com.leancoffree.backend.enums.TopicStatus.DISCUSSING;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import com.leancoffree.backend.domain.model.RefreshTopicsRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.enums.SortTopicsBy;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.TopicsRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RefreshTopicsServiceImpl implements RefreshTopicsService {

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

  public SuccessOrFailureAndErrorBody refreshTopics(
      final RefreshTopicsRequest refreshTopicsRequest) {

    if (NEXT.equals(refreshTopicsRequest.getCommand())) {

      final Optional<TopicsEntity> currentTopicsEntityOptional = topicsRepository
          .findById(new TopicsId(refreshTopicsRequest.getSessionId(),
              refreshTopicsRequest.getCurrentTopicText(), refreshTopicsRequest.getCurrentTopicAuthorDisplayName()));
      final Optional<TopicsEntity> nextTopicsEntityOptional = topicsRepository
          .findById(new TopicsId(refreshTopicsRequest.getSessionId(),
              refreshTopicsRequest.getNextTopicText(), refreshTopicsRequest.getNextTopicAuthorDisplayName()));
      final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository
          .findById(refreshTopicsRequest.getSessionId());

      if (currentTopicsEntityOptional.isPresent() && nextTopicsEntityOptional.isPresent()
          && sessionsEntityOptional.isPresent()) {
        final TopicsEntity currentTopicsEntity = currentTopicsEntityOptional.get();
        currentTopicsEntity.setTopicStatus(DISCUSSED);
        topicsRepository.save(currentTopicsEntity);

        final TopicsEntity nextTopicsEntity = nextTopicsEntityOptional.get();
        nextTopicsEntity.setTopicStatus(DISCUSSING);
        topicsRepository.save(nextTopicsEntity);

        final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
        sessionsEntity.setCurrentTopicEndTime(Instant.now().plusSeconds(180));
        sessionsRepository.save(sessionsEntity);

        broadcastTopicsService.broadcastTopics(refreshTopicsRequest.getSessionId(), VOTES, false);
        return new SuccessOrFailureAndErrorBody(SUCCESS, null);
      }
    } else if (FINISH.equals(refreshTopicsRequest.getCommand())) {
      return new SuccessOrFailureAndErrorBody(FAILURE, "Mocked finish!");
    }
    return new SuccessOrFailureAndErrorBody(FAILURE, "Command or topic/session invalid");
  }
}
