package com.leancoffree.backend.service;

import com.leancoffree.backend.controller.RefreshUsersInSessionException;
import com.leancoffree.backend.domain.model.ListOfDisplayNamesBody;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;

public interface RefreshUsersInSessionService {

  ListOfDisplayNamesBody refreshUsersInSession(final RefreshUsersRequest refreshUsersRequest)
      throws RefreshUsersInSessionException;
}
