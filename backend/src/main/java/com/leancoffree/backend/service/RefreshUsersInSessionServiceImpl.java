package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.RefreshUsersCommand.ADD;

import com.leancoffree.backend.controller.RefreshUsersInSessionException;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import org.springframework.stereotype.Service;

@Service
public class RefreshUsersInSessionServiceImpl implements RefreshUsersInSessionService {

  private final AddUserToSessionService addUserToSessionService;
  private final DropUserInSessionService dropUserInSessionService;

  public RefreshUsersInSessionServiceImpl(final AddUserToSessionService addUserToSessionService,
      final DropUserInSessionService dropUserInSessionService) {
    this.addUserToSessionService = addUserToSessionService;
    this.dropUserInSessionService = dropUserInSessionService;
  }

  public String refreshUsersInSession(
      final RefreshUsersRequest refreshUsersRequest) throws RefreshUsersInSessionException {
    return ADD.equals(refreshUsersRequest.getCommand())
        ? addUserToSessionService.addUserToSessionAndReturnAllUsers(refreshUsersRequest)
        : dropUserInSessionService.dropUserInSessionAndReturnAllUsers(refreshUsersRequest);
  }
}
