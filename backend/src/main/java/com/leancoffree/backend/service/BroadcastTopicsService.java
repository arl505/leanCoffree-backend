package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.enums.SortTopicsBy;

public interface BroadcastTopicsService {

  SuccessOrFailureAndErrorBody broadcastTopics(final String sessionId,
      final SortTopicsBy sortTopicsBy);
}
