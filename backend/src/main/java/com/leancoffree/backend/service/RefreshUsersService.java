package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SessionStatusResponse;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface RefreshUsersService {

  SessionStatusResponse refreshUsers(final RefreshUsersRequest refreshUsersRequest);

  SuccessOrFailureAndErrorBody quickRefresh(final String sessionId);
}
