package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.SessionStatus.STARTED;
import static com.leancoffree.backend.enums.SessionVerificationStatus.VERIFICATION_FAILURE;
import static com.leancoffree.backend.enums.SessionVerificationStatus.VERIFICATION_SUCCESS;

import com.leancoffree.backend.domain.entity.SessionsEntity;
import com.leancoffree.backend.domain.model.SessionDetails;
import com.leancoffree.backend.domain.model.VerifySessionResponse;
import com.leancoffree.backend.repository.SessionsRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class VerifySessionServiceImpl implements VerifySessionService {

  private final SessionsRepository sessionsRepository;

  public VerifySessionServiceImpl(final SessionsRepository sessionsRepository) {
    this.sessionsRepository = sessionsRepository;
  }

  public VerifySessionResponse verifySession(final String sessionId) {
    final Optional<SessionsEntity> sessionsEntityOptional = sessionsRepository.findById(sessionId);

    VerifySessionResponse verifySessionResponse;
    if (sessionsEntityOptional.isPresent() && sessionsEntityOptional.get().getSessionStatus()
        .equals(STARTED)) {
      verifySessionResponse = VerifySessionResponse.builder()
          .verificationStatus(VERIFICATION_SUCCESS)
          .sessionDetails(SessionDetails.builder()
              .sessionId(sessionId)
              .sessionStatus(STARTED)
              .build())
          .build();
    } else {
      verifySessionResponse = VerifySessionResponse.builder()
          .verificationStatus(VERIFICATION_FAILURE)
          .build();
    }
    return verifySessionResponse;
  }

}
