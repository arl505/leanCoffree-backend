package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface TransitionToDiscussionService {

  SuccessOrFailureAndErrorBody transitionToDiscussion(final String sessionId,
      final String displayName);
}
