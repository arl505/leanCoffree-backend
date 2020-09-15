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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.util.Pair;
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

    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository.findById(sessionId);
    if (sessionsEntityOptional.isPresent()) {

      final Map<String, TopicDetails> topicsAndVotersMap = new LinkedHashMap<>();
      for (final Object[] objects : votesList) {
        final String text = (String) objects[0];
        final List<String> voters = topicsAndVotersMap.containsKey(text)
            ? topicsAndVotersMap.get(text).getVoters()
            : new ArrayList<>();
        if (objects[1] != null) {
          voters.add((String) objects[1]);
        }
        topicsAndVotersMap
            .put(text, new TopicDetails((String) objects[3], (String) objects[2], voters));
      }

      final JSONArray discussionBacklogTopicsJson = new JSONArray();
      final JSONArray discussedTopicsJson = new JSONArray();
      final JSONObject currentDiscussionItem = new JSONObject();
      for (final Map.Entry<String, TopicDetails> entry : topicsAndVotersMap
          .entrySet()) {
        if (entry.getValue().getTopicStatus().equals("QUEUED")) {
          discussionBacklogTopicsJson.put(new JSONObject()
              .put("text", entry.getKey())
              .put("authorDisplayName", entry.getValue().getAuthorDisplayName())
              .put("voters", new JSONArray(entry.getValue().getVoters())));
        } else if (entry.getValue().getTopicStatus().equals("DISCUSSED")) {
          discussedTopicsJson.put(new JSONObject()
              .put("text", entry.getKey())
              .put("voters", new JSONArray(entry.getValue().getVoters())));
        } else if (entry.getValue().getTopicStatus().equals("DISCUSSING")) {
          currentDiscussionItem.put("text", entry.getKey())
              .put("voters", new JSONArray(entry.getValue().getVoters()))
              .put("authorDisplayName", entry.getValue().getAuthorDisplayName())
              .put("endTime", sessionsEntityOptional.get().getCurrentTopicEndTime() == null
                  ? JSONObject.NULL
                  : sessionsEntityOptional.get().getCurrentTopicEndTime());
        }
      }

      final JSONObject messageJson = new JSONObject()
          .put("currentDiscussionItem", currentDiscussionItem)
          .put("discussionBacklogTopics", discussionBacklogTopicsJson)
          .put("discussedTopics", discussedTopicsJson);

      webSocketMessagingTemplate
          .convertAndSend(websocketDestination + sessionId, messageJson.toString());
      return new SuccessOrFailureAndErrorBody(SUCCESS, null);
    } else {
      return new SuccessOrFailureAndErrorBody(FAILURE, "Couldn't find that session");
    }
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  private static class TopicDetails {

    private String authorDisplayName;
    private String topicStatus;
    private List<String> voters;
  }
}
