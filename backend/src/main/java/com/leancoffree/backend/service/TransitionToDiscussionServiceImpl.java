package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SortTopicsBy.Y_INDEX;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.enums.SessionStatus;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.TopicsRepository;
import com.leancoffree.backend.repository.VotesRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TransitionToDiscussionServiceImpl implements TransitionToDiscussionService {

  @Value("${defaultTopicTime:180}")
  private Integer defaultTopicTime;

  private final SessionsRepository sessionsRepository;
  private final TopicsRepository topicsRepository;
  private final VotesRepository votesRepository;
  private final BroadcastTopicsService broadcastTopicsService;

  public TransitionToDiscussionServiceImpl(final SessionsRepository sessionsRepository,
      final TopicsRepository topicsRepository,
      final VotesRepository votesRepository,
      final BroadcastTopicsService broadcastTopicsService) {
    this.sessionsRepository = sessionsRepository;
    this.topicsRepository = topicsRepository;
    this.votesRepository = votesRepository;
    this.broadcastTopicsService = broadcastTopicsService;
  }

  public SuccessOrFailureAndErrorBody transitionToDiscussion(final String sessionId) {
    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository.findById(sessionId);
    if (sessionsEntityOptional.isEmpty()) {
      return new SuccessOrFailureAndErrorBody(FAILURE, "Couldn't find session with that ID");
    }

    final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
    sessionsEntity.setSessionStatus(SessionStatus.DISCUSSING);
    sessionsEntity.setCurrentTopicEndTime(Instant.now().plusSeconds(defaultTopicTime));
    sessionsRepository.save(sessionsEntity);

    int y = 0;
    final Map<String, Boolean> topicsWithVotes = new HashMap<>();
    final List<Object[]> objects = votesRepository.findVotesInOrder(sessionId);
    for (; y < objects.size(); y++) {
      topicsWithVotes.put((String) objects.get(y)[0], true);
      topicsRepository.updateYIndexByTextAndSessionId(y, (String) objects.get(y)[0], sessionId);
    }

    final List<TopicsEntity> topics = topicsRepository
        .findAllBySessionIdOrderByText(sessionId);
    for(final TopicsEntity topicsEntity : topics) {
      if(topicsWithVotes.get(topicsEntity.getText()) == null) {
        topicsRepository.updateYIndexByTextAndSessionId(y, topicsEntity.getText(), sessionId);
        y++;
      }
    }

    broadcastTopicsService.broadcastTopics(sessionId, Y_INDEX, true);

    return new SuccessOrFailureAndErrorBody(SUCCESS, null);
  }
}
