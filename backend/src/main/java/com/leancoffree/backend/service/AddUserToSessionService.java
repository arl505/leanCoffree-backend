package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.NewUserRequestNotification;
import com.leancoffree.backend.domain.model.NewUserResponseNotification;

public interface AddUserToSessionService {

  NewUserResponseNotification addUserToSessionAndReturnAllUsers(
      final NewUserRequestNotification newUserRequestNotification);
}
