package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface RefreshUsersService {

  SuccessOrFailureAndErrorBody refreshUsers(final RefreshUsersRequest refreshUsersRequest);

  SuccessOrFailureAndErrorBody quickRefresh(final String sessionId);
}
