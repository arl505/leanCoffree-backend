package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.TopicsRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
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
    final List<Object[]> votesList = topicsRepository.findAllVotes(sessionId);

    final Map<String, List<String>> topicsAndVotersMap = new TreeMap<>();
    for (final Object[] objects : votesList) {
      final String text = (String) objects[0];
      final List<String> voters = topicsAndVotersMap.containsKey(text)
        ? topicsAndVotersMap.get(text)
        : new ArrayList<>();
      if(objects[1] != null) {
        voters.add((String) objects[1]);
      }
      topicsAndVotersMap.put(text, voters);
    }

    final JSONArray messageJson = new JSONArray();
    for(final Map.Entry<String, List<String>> entry : topicsAndVotersMap.entrySet()) {
      messageJson.put(new JSONObject()
          .put("text", entry.getKey())
          .put("voters", new JSONArray(entry.getValue())));
    }

    webSocketMessagingTemplate
        .convertAndSend(websocketDestination + sessionId, messageJson.toString());
    return new SuccessOrFailureAndErrorBody(SUCCESS, null);
  }
}
