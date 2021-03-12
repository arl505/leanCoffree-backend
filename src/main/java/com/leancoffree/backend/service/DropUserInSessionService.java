package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SessionStatusResponse;

public interface DropUserInSessionService {

  SessionStatusResponse dropUserInSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest);
}
