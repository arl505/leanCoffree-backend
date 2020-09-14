package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SessionStatusResponse;

public interface AddUserToSessionService {

  SessionStatusResponse addUserToSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest);
}
