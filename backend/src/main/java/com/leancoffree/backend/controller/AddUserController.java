package com.leancoffree.backend.controller;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.model.DisplayNameAndSessionIdBody;
import com.leancoffree.backend.domain.model.ListOfDisplayNamesBody;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.service.AddUserToSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AddUserController {

  private final AddUserToSessionService addUserToSessionService;

  public AddUserController(final AddUserToSessionService addUserToSessionService) {
    this.addUserToSessionService = addUserToSessionService;
  }

  @PostMapping("/add-user-to-session")
  public ResponseEntity<SuccessOrFailureAndErrorBody> addUserHttpEndpoint(
      @RequestBody final DisplayNameAndSessionIdBody displayNameAndSessionIdBody) {

    final ListOfDisplayNamesBody listOfDisplayNamesBody;
    try {
      listOfDisplayNamesBody = addUserToSessionService
          .addUserToSessionAndReturnAllUsers(displayNameAndSessionIdBody);
    } catch (Exception e) {
      return ResponseEntity.ok(SuccessOrFailureAndErrorBody.builder()
          .status(FAILURE)
          .error(e.getMessage())
          .build());
    }

    refreshUsersList(listOfDisplayNamesBody);
    return ResponseEntity.ok(SuccessOrFailureAndErrorBody.builder()
        .status(SUCCESS)
        .build());
  }

  @SendTo("/topic/users")
  public ListOfDisplayNamesBody refreshUsersList(
      final ListOfDisplayNamesBody listOfDisplayNamesBody) {
    return listOfDisplayNamesBody;
  }

}