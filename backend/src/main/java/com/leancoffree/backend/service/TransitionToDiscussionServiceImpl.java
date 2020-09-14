package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.enums.SessionStatus;
import com.leancoffree.backend.repository.SessionsRepository;
import java.util.Optional;
import org.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransitionToDiscussionServiceImpl implements TransitionToDiscussionService {

  private final SessionsRepository sessionsRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public TransitionToDiscussionServiceImpl(final SessionsRepository sessionsRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.sessionsRepository = sessionsRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  public SuccessOrFailureAndErrorBody transitionToDiscussion(final String sessionId) {
    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository.findById(sessionId);
    if(sessionsEntityOptional.isEmpty()) {
      return new SuccessOrFailureAndErrorBody(FAILURE, "Couldn't find session with that ID");
    }

    final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
    sessionsEntity.setSessionStatus(SessionStatus.DISCUSSING);
    sessionsRepository.save(sessionsEntity);

    webSocketMessagingTemplate
        .convertAndSend("/topic/status/session/" + sessionId, "DISCUSSING");
    return new SuccessOrFailureAndErrorBody(SUCCESS, null);
  }
}
