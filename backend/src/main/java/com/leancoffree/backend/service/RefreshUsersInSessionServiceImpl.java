package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.RefreshUsersCommand.ADD;
import static com.leancoffree.backend.enums.RefreshUsersCommand.DROP;

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

  public void refreshUsersInSession(
      final RefreshUsersRequest refreshUsersRequest) throws RefreshUsersInSessionException {
    if(ADD.equals(refreshUsersRequest.getCommand())) {
      addUserToSessionService.addUserToSessionAndReturnAllUsers(refreshUsersRequest);
    } else if(DROP.equals(refreshUsersRequest.getCommand())) {
      dropUserInSessionService.dropUserInSessionAndReturnAllUsers(refreshUsersRequest);
    }
  }
}
