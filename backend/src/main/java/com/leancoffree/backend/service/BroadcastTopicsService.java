package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface BroadcastTopicsService {

  SuccessOrFailureAndErrorBody broadcastTopics(final String sessionId);
}
