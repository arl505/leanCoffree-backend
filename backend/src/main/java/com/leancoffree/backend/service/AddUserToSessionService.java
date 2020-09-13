package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface AddUserToSessionService {

  SuccessOrFailureAndErrorBody addUserToSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest);
}
