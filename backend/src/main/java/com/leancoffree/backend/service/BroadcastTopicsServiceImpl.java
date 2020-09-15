package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.TopicsRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BroadcastTopicsServiceImpl implements BroadcastTopicsService {

  private static final String websocketDestination = "/topic/discussion-topics/session/";

  private final TopicsRepository topicsRepository;
  private final SessionsRepository sessionsRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public BroadcastTopicsServiceImpl(final TopicsRepository topicsRepository,
      final SessionsRepository sessionsRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.topicsRepository = topicsRepository;
    this.sessionsRepository = sessionsRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  public SuccessOrFailureAndErrorBody broadcastTopics(final String sessionId) {
    final List<Object[]> votesList = topicsRepository.findAllVotes(sessionId);

    final Map<String, List<String>> topicsAndVotersMap = new LinkedHashMap<>();
    for (final Object[] objects : votesList) {
      final String text = (String) objects[0];
      final List<String> voters = topicsAndVotersMap.containsKey(text)
          ? topicsAndVotersMap.get(text)
          : new ArrayList<>();
      if (objects[1] != null) {
        voters.add((String) objects[1]);
      }
      topicsAndVotersMap.put(text, voters);
    }

    final JSONArray topicsJson = new JSONArray();
    for (final Map.Entry<String, List<String>> entry : topicsAndVotersMap.entrySet()) {
      topicsJson.put(new JSONObject()
          .put("text", entry.getKey())
          .put("voters", new JSONArray(entry.getValue())));
    }

    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository.findById(sessionId);
    if (sessionsEntityOptional.isPresent()) {
      final JSONObject messageJson = new JSONObject()
          .put("currentTopicEndTime",
              sessionsEntityOptional.get().getCurrentTopicEndTime() == null
                  ? JSONObject.NULL
                  : sessionsEntityOptional.get().getCurrentTopicEndTime())
          .put("topics", topicsJson);

      webSocketMessagingTemplate
          .convertAndSend(websocketDestination + sessionId, messageJson.toString());
      return new SuccessOrFailureAndErrorBody(SUCCESS, null);
    } else {
      return new SuccessOrFailureAndErrorBody(FAILURE, "Couldn't find that session");
    }
  }
}
