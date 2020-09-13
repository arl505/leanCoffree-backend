package com.leancoffree.backend.service;

import static com.leancoffree.backend.enums.RefreshUsersCommand.ADD;

import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import org.springframework.stereotype.Service;

@Service
public class RefreshUsersServiceImpl implements RefreshUsersService {

  private final AddUserToSessionService addUserToSessionService;
  private final DropUserInSessionService dropUserInSessionService;

  public RefreshUsersServiceImpl(final AddUserToSessionService addUserToSessionService,
      final DropUserInSessionService dropUserInSessionService) {
    this.addUserToSessionService = addUserToSessionService;
    this.dropUserInSessionService = dropUserInSessionService;
  }

  public SuccessOrFailureAndErrorBody refreshUsers(final RefreshUsersRequest refreshUsersRequest) {
    if (ADD.equals(refreshUsersRequest.getCommand())) {
      return addUserToSessionService.addUserToSessionAndReturnAllUsers(refreshUsersRequest);
    } else {
      return dropUserInSessionService.dropUserInSessionAndReturnAllUsers(refreshUsersRequest);
    }
  }
}
