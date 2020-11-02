package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.repository.SessionsRepository;
import com.leancoffree.backend.repository.TopicsRepository;
import com.leancoffree.backend.repository.UsersRepository;
import com.leancoffree.backend.repository.VotesRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class EndSessionServiceImpl implements EndSessionService {

  @Value("${frontendBaseUrl:https://leanCoffree.com}")
  private String frontendBaseUrl;

  private static final String websocketDestination = "/topic/discussion-topics/session/";

  private final SessionsRepository sessionsRepository;
  private final TopicsRepository topicsRepository;
  private final VotesRepository votesRepository;
  private final UsersRepository usersRepository;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public EndSessionServiceImpl(final SessionsRepository sessionsRepository,
      final TopicsRepository topicsRepository,
      final VotesRepository votesRepository,
      final UsersRepository usersRepository,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.sessionsRepository = sessionsRepository;
    this.topicsRepository = topicsRepository;
    this.votesRepository = votesRepository;
    this.usersRepository = usersRepository;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  @Transactional
  public SuccessOrFailureAndErrorBody endSession(final String sessionId) {
    votesRepository.deleteByVoterSessionId(sessionId);
    topicsRepository.deleteBySessionId(sessionId);
    usersRepository.deleteBySessionId(sessionId);
    sessionsRepository.deleteById(sessionId);
    webSocketMessagingTemplate.convertAndSend(websocketDestination + sessionId, "");
    return new SuccessOrFailureAndErrorBody(SUCCESS, null);
  }
}
