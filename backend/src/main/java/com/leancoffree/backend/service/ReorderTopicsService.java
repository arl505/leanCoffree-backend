package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.ReorderTopicsRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface ReorderTopicsService {

  SuccessOrFailureAndErrorBody reorderTopics(final ReorderTopicsRequest reorderTopicsRequest);
}
