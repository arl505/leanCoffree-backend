package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.VerifySessionResponse;

public interface VerifySessionService {

  VerifySessionResponse verifySession(final String sessionId);
}
