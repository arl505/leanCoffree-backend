package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.TopicsEntity;
import com.leancoffree.backend.domain.entity.TopicsEntity.TopicsId;
import com.leancoffree.backend.domain.model.SubmitTopicRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.TopicsRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SubmitTopicServiceImpl implements SubmitTopicService {

  private static final String websocketDestination = "/topic/discussion-topics/session/";

  private final SimpMessagingTemplate webSocketMessagingTemplate;
  private final TopicsRepository topicsRepository;

  public SubmitTopicServiceImpl(final SimpMessagingTemplate webSocketMessagingTemplate,
      final TopicsRepository topicsRepository) {
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
    this.topicsRepository = topicsRepository;
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

      final Optional<List<TopicsEntity>> optionalTopicsEntityList = topicsRepository
          .findAllBySessionId(sessionId);

      if (optionalTopicsEntityList.isPresent()) {
        final List<String> displayNames = new ArrayList<>();
        for (final TopicsEntity usersEntity : optionalTopicsEntityList.get()) {
          displayNames.add(usersEntity.getText());
        }
        final String websocketMessageString = new JSONObject()
            .put("topics", new JSONArray(displayNames)).toString();
        webSocketMessagingTemplate
            .convertAndSend(websocketDestination + sessionId, websocketMessageString);
        return new SuccessOrFailureAndErrorBody(SUCCESS, null);
      } else {
        return SuccessOrFailureAndErrorBody.builder()
            .status(FAILURE)
            .error("How'd that happen? Please try again")
            .build();
      }
    } else {
      return SuccessOrFailureAndErrorBody.builder()
          .status(FAILURE)
          .error("Topic already submitted")
          .build();
    }
  }
}













