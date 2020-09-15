package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.RefreshTopicsRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface RefreshTopicsService {

  SuccessOrFailureAndErrorBody refreshTopics(final RefreshTopicsRequest refreshTopicsRequest);
}
