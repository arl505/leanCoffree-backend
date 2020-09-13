package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;

import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import com.leancoffree.backend.domain.model.SubmitTopicRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.TopicsRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SubmitTopicServiceImpl implements SubmitTopicService {

  private final TopicsRepository topicsRepository;
  private final BroadcastTopicsService broadcastTopicsService;

  public SubmitTopicServiceImpl(final TopicsRepository topicsRepository,
      final BroadcastTopicsService broadcastTopicsService) {
    this.topicsRepository = topicsRepository;
    this.broadcastTopicsService = broadcastTopicsService;
  }

  public SuccessOrFailureAndErrorBody submitTopic(final SubmitTopicRequest submitTopicRequest) {

    final String text = submitTopicRequest.getSubmissionText();
    final String sessionId = submitTopicRequest.getSessionId();

    final TopicsId topicsId = new TopicsId(sessionId, text);

    final Optional<TopicsEntity> topicsEntityOptional = topicsRepository.findById(topicsId);

    if (topicsEntityOptional.isEmpty()) {
      topicsRepository.save(TopicsEntity.builder()
          .text(text)
          .sessionId(sessionId)
          .build());
      return broadcastTopicsService.broadcastTopics(sessionId);
    } else {
      return new SuccessOrFailureAndErrorBody(FAILURE, "Topic already submitted");
    }
  }
}
