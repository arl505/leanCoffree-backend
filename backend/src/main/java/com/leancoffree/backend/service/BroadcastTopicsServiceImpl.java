package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.TopicsRepository;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BroadcastTopicsServiceImpl implements BroadcastTopicsService {

  private static final String websocketDestination = "/topic/discussion-topics/session/";

  private final TopicsRepository topicsRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public BroadcastTopicsServiceImpl(final TopicsRepository topicsRepository,
      SimpMessagingTemplate webSocketMessagingTemplate) {
    this.topicsRepository = topicsRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  public SuccessOrFailureAndErrorBody broadcastTopics(final String sessionId) {
    final Optional<List<TopicsEntity>> optionalTopicsEntityList = topicsRepository
        .findAllBySessionIdOrderByCreatedTimestamp(sessionId);

    if (optionalTopicsEntityList.isPresent()) {
      final JSONArray usersJsonArray = new JSONArray();
      for (final TopicsEntity topicsEntity : optionalTopicsEntityList.get()) {
        usersJsonArray.put(new JSONObject().put("votes", 0).put("text", topicsEntity.getText()));
      }
      webSocketMessagingTemplate
          .convertAndSend(websocketDestination + sessionId, usersJsonArray.toString());
      return new SuccessOrFailureAndErrorBody(SUCCESS, null);
    } else {
      return new SuccessOrFailureAndErrorBody(FAILURE, "No users to broadcast");
    }
  }
}
