package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.DisplayNameAndSessionIdBody;
import com.leancoffree.backend.domain.model.ListOfDisplayNamesBody;

public interface AddUserToSessionService {

  ListOfDisplayNamesBody addUserToSessionAndReturnAllUsers(
      final DisplayNameAndSessionIdBody displayNameAndSessionIdBody) throws Exception;
}
