package com.leancoffree.backend.service;

import com.leancoffree.backend.domain.model.AddUserToSessionRequest;
import com.leancoffree.backend.domain.model.AddUserToSessionResponse;

public interface AddUserToSessionService {

  AddUserToSessionResponse addUserToSession(final AddUserToSessionRequest addUserToSessionRequest);
}
