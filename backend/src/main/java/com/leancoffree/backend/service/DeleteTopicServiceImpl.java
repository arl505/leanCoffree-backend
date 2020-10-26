package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SortTopicsBy.CREATION;
import static com.leancoffree.backend.enums.SortTopicsBy.VOTES;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import com.leancoffree.backend.domain.model.DeleteTopicRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.enums.SessionStatus;
import com.leancoffree.backend.enums.SortTopicsBy;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.TopicsRepository;
import com.leancoffree.backend.repository.VotesRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteTopicServiceImpl implements DeleteTopicService {

  private final VotesRepository votesRepository;
  private final TopicsRepository topicsRepository;
  private final SessionsRepository sessionsRepository;
  private final BroadcastTopicsService broadcastTopicsService;

  public DeleteTopicServiceImpl(final VotesRepository votesRepository,
      final TopicsRepository topicsRepository,
      final SessionsRepository sessionsRepository,
      final BroadcastTopicsService broadcastTopicsService) {
    this.votesRepository = votesRepository;
    this.topicsRepository = topicsRepository;
    this.sessionsRepository = sessionsRepository;
    this.broadcastTopicsService = broadcastTopicsService;
  }

  @Transactional
  public SuccessOrFailureAndErrorBody deleteTopic(final DeleteTopicRequest deleteTopicRequest) {
    votesRepository.deleteByVoterSessionIdAndText(deleteTopicRequest.getSessionId(),
        deleteTopicRequest.getTopicText());

    final TopicsId topicsId = TopicsId.builder()
        .text(deleteTopicRequest.getTopicText())
        .sessionId(deleteTopicRequest.getSessionId())
        .displayName(deleteTopicRequest.getAuthorName())
        .build();
    topicsRepository.deleteById(topicsId);

    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository
        .findById(deleteTopicRequest.getSessionId());

    if (sessionsEntityOptional.isEmpty()) {
      return new SuccessOrFailureAndErrorBody(FAILURE, "Couldn't find session");
    }

    final SortTopicsBy sortTopicsBy =
        sessionsEntityOptional.get().getSessionStatus() == SessionStatus.STARTED
            ? CREATION
            : VOTES;

    broadcastTopicsService.broadcastTopics(deleteTopicRequest.getSessionId(), sortTopicsBy, false);
    return new SuccessOrFailureAndErrorBody(SUCCESS, null);
  }
}
