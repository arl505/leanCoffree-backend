package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SessionStatus.STARTED;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.repository.SessionsRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StartSessionServiceImpl implements StartSessionService {

  private final SessionsRepository sessionsRepository;

  public StartSessionServiceImpl(final SessionsRepository sessionsRepository) {
    this.sessionsRepository = sessionsRepository;
  }

  public void startSession(String sessionId) {
    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository.findById(sessionId);
    if(sessionsEntityOptional.isPresent()) {
      final SessionsEntity sessionsEntity = sessionsEntityOptional.get();
      sessionsEntity.setSessionStatus(STARTED);
      sessionsRepository.save(sessionsEntity);
    }
  }
}
