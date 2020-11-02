package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface EndSessionService {

  SuccessOrFailureAndErrorBody endSession(final String sessionId);
}
