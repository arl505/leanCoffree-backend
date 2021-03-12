package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SortTopicsBy.Y_INDEX;
import static com.leancoffree.backend.enums.SuccessOrFailure.*;

import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.model.ReorderTopicsRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.TopicsRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReorderTopicsServiceImpl implements ReorderTopicsService {

  private final TopicsRepository topicsRepository;
  private final BroadcastTopicsService broadcastTopicsService;

  public ReorderTopicsServiceImpl(final TopicsRepository topicsRepository,
      final BroadcastTopicsService broadcastTopicsService) {
    this.topicsRepository = topicsRepository;
    this.broadcastTopicsService = broadcastTopicsService;
  }

  public SuccessOrFailureAndErrorBody reorderTopics(
      final ReorderTopicsRequest reorderTopicsRequest) {

    final List<TopicsEntity> topics = topicsRepository
        .findAllBySessionIdOrderByVerticalIndex(reorderTopicsRequest.getSessionId());

    boolean topicFound = false;
    TopicsEntity topicToReorder = null;
    for (int i = 0; i < topics.size() && !topicFound; i++) {
      if (topics.get(i).getText().equals(reorderTopicsRequest.getText())) {
        topicToReorder = topics.get(i);
        topics.remove(i);
        topicFound = true;
      }
    }

    if (topicToReorder != null) {
      topics.add(reorderTopicsRequest.getNewIndex(), topicToReorder);
      for (int i = 0; i < topics.size(); i++) {
        topics.get(i).setVerticalIndex(i);
        topicsRepository.save(topics.get(i));
      }
      broadcastTopicsService.broadcastTopics(reorderTopicsRequest.getSessionId(), Y_INDEX, false);
      return new SuccessOrFailureAndErrorBody(SUCCESS, null);
    }
    return new SuccessOrFailureAndErrorBody(FAILURE, "Couldn't find topic to reorder");
  }
}
