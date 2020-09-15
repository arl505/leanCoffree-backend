package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;
import static com.leancoffree.backend.enums.TopicStatus.DISCUSSING;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.enums.SessionStatus;
import com.leancoffree.backend.enums.TopicStatus;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.TopicsRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TransitionToDiscussionServiceImpl implements TransitionToDiscussionService {

  private final SessionsRepository sessionsRepository;
  private final TopicsRepository topicsRepository;
  private final BroadcastTopicsService broadcastTopicsService;

  public TransitionToDiscussionServiceImpl(final SessionsRepository sessionsRepository,
      final BroadcastTopicsService broadcastTopicsService,
      final TopicsRepository topicsRepository) {
    this.sessionsRepository = sessionsRepository;
    this.broadcastTopicsService = broadcastTopicsService;
    this.topicsRepository = topicsRepository;
  }

  public SuccessOrFailureAndErrorBody transitionToDiscussion(final String sessionId) {
    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository.findById(sessionId);
    if(sessionsEntityOptional.isEmpty()) {
      return new SuccessOrFailureAndErrorBody(FAILURE, "Couldn't find session with that ID");
    }

    final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
    sessionsEntity.setSessionStatus(SessionStatus.DISCUSSING);
    sessionsEntity.setCurrentTopicEndTime(Instant.now().plusSeconds(5));
    sessionsRepository.save(sessionsEntity);

    final List<Object[]> resultObjects = topicsRepository.findAllVotes(sessionId);
    final TopicsEntity topicsEntity = TopicsEntity.builder()
        .sessionId(sessionId)
        .text((String) resultObjects.get(0)[0])
        .topicStatus(DISCUSSING)
        .build();
    topicsRepository.save(topicsEntity);

    broadcastTopicsService.broadcastTopics(sessionId);
    return new SuccessOrFailureAndErrorBody(SUCCESS, null);
  }
}
