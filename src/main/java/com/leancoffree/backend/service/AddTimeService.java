package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.AddTimeRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface AddTimeService {

  SuccessOrFailureAndErrorBody addTime(final AddTimeRequest addTimeRequest);
}
