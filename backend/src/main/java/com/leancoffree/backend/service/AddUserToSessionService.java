package com.leancoffree.backend.service;

import com.leancoffree.backend.controller.RefreshUsersInSessionException;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;

public interface AddUserToSessionService {

  String addUserToSessionAndReturnAllUsers(final RefreshUsersRequest refreshUsersRequest)
      throws RefreshUsersInSessionException;
}
