package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SessionStatus.STARTED;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.model.CreateSessionResponse;
import com.leancoffree.backend.repository.SessionsRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateSessionServiceImpl implements CreateSessionService {

  private final SessionsRepository sessionsRepository;

  public CreateSessionServiceImpl(final SessionsRepository sessionsRepository) {
    this.sessionsRepository = sessionsRepository;
  }

  public CreateSessionResponse createSession() {
    String sessionId = null;

    while(sessionId == null) {
      final String guid = UUID.randomUUID().toString();
      if(sessionsRepository.findById(guid).isEmpty()) {
        sessionId = guid;
      }
    }

    sessionsRepository.save(SessionsEntity.builder()
        .id(sessionId)
        .sessionStatus(STARTED)
        .build());

    return CreateSessionResponse.builder()
        .id(sessionId)
        .build();
  }
}
