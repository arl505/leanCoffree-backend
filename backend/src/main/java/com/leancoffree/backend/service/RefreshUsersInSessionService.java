package com.leancoffree.backend.service;

import com.leancoffree.backend.controller.RefreshUsersInSessionException;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import org.json.JSONArray;

public interface RefreshUsersInSessionService {

  JSONArray refreshUsersInSession(final RefreshUsersRequest refreshUsersRequest)
      throws RefreshUsersInSessionException;
}
