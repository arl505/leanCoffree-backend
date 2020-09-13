package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;

public interface DropUserInSessionService {

  SuccessOrFailureAndErrorBody dropUserInSessionAndReturnAllUsers(
      final RefreshUsersRequest refreshUsersRequest);
}
