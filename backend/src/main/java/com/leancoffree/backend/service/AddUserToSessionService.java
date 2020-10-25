package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.AddUserResponse;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;

public interface AddUserToSessionService {

  AddUserResponse addUserToSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest);
}
