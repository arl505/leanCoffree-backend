package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.SubmitTopicRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface SubmitTopicService {

  SuccessOrFailureAndErrorBody submitTopic(final SubmitTopicRequest submitTopicRequest);
}
