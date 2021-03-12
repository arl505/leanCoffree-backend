package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.DeleteTopicRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface DeleteTopicService {

  SuccessOrFailureAndErrorBody deleteTopic(final DeleteTopicRequest deleteTopicRequest);
}
