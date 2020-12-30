package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SortTopicsBy.CREATION;
import static com.leancoffree.backend.enums.SortTopicsBy.Y_INDEX;
import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;
import static com.leancoffree.backend.enums.TopicStatus.DISCUSSING;
import static java.time.temporal.ChronoField.*;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.enums.SortTopicsBy;
import com.leancoffree.backend.enums.TopicStatus;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.TopicsRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
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

  public SuccessOrFailureAndErrorBody broadcastTopics(final String sessionId,
      final SortTopicsBy sortTopicsBy, boolean shouldUpdateTopicStatus) {

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
            .put(text, new TopicDetails(text, (String) objects[3], (String) objects[2], voters,
                ((Timestamp) objects[4]).toInstant(), (Integer) objects[5],
                (Timestamp) objects[6]));
      }

      final List<TopicDetails> topicDetailsList = new ArrayList<>();
      for (final Map.Entry<String, TopicDetails> entry : topicsAndVotersMap.entrySet()) {
        topicDetailsList.add(entry.getValue());
      }

      if (Y_INDEX.equals(sortTopicsBy)) {
        topicDetailsList.sort(Comparator.comparing(TopicDetails::getYIndex));
      } else if (CREATION.equals(sortTopicsBy)) {
        topicDetailsList.sort(Comparator.comparing(TopicDetails::getCreationDate));
      }

      if (shouldUpdateTopicStatus) {
        final TopicDetails currentDiscussionItem = topicDetailsList.get(0);
        currentDiscussionItem.setTopicStatus(DISCUSSING.toString());
        topicDetailsList.set(0, currentDiscussionItem);
        final TopicsEntity topicsEntity = TopicsEntity.builder()
            .sessionId(sessionId)
            .text(currentDiscussionItem.getText())
            .topicStatus(TopicStatus.valueOf(currentDiscussionItem.getTopicStatus()))
            .displayName(currentDiscussionItem.getAuthorDisplayName())
            .createdTimestamp(currentDiscussionItem.getCreationDate())
            .verticalIndex(999)
            .build();
        topicsRepository.save(topicsEntity);
      }

      final JSONObject currentDiscussionItem = new JSONObject();
      final JSONArray discussionBacklogTopicsJson = new JSONArray();
      final List<JSONObject> discussedTopicsList = new ArrayList<>();
      for (final TopicDetails topicDetails : topicDetailsList) {
        if (topicDetails.getTopicStatus().equals("QUEUED")) {
          discussionBacklogTopicsJson.put(new JSONObject()
              .put("text", topicDetails.getText())
              .put("authorDisplayName", topicDetails.getAuthorDisplayName())
              .put("voters", new JSONArray(topicDetails.getVoters())));
        } else if (topicDetails.getTopicStatus().equals("DISCUSSED")) {
          discussedTopicsList.add(new JSONObject()
              .put("text", topicDetails.getText())
              .put("voters", new JSONArray(topicDetails.getVoters()))
              .put("authorDisplayName", topicDetails.getAuthorDisplayName())
              .put("finishedAt", topicDetails.getFinishedAt()));
        } else if (topicDetails.getTopicStatus().equals("DISCUSSING")) {
          currentDiscussionItem.put("text", topicDetails.getText())
              .put("voters", new JSONArray(topicDetails.getVoters()))
              .put("authorDisplayName", topicDetails.getAuthorDisplayName())
              .put("endTime", sessionsEntityOptional.get().getCurrentTopicEndTime() == null
                  ? JSONObject.NULL
                  : sessionsEntityOptional.get().getCurrentTopicEndTime().with(NANO_OF_SECOND, 0));
        }
      }

      discussedTopicsList.sort(Comparator.comparing(x -> ((Timestamp) x.get("finishedAt"))));

      final JSONArray discussedTopicsJson = new JSONArray(discussedTopicsList);

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

    private String text;
    private String authorDisplayName;
    private String topicStatus;
    private List<String> voters;
    private Instant creationDate;
    private Integer yIndex;
    private Timestamp finishedAt;
  }
}
