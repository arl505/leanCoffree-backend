package com.leancoffree.backend.controller;

import com.leancoffree.backend.domain.model.NewUserRequestNotification;
import com.leancoffree.backend.domain.model.NewUserResponseNotification;
import com.leancoffree.backend.service.AddUserToSessionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class AddUserController {

  private final AddUserToSessionService addUserToSessionService;

  public AddUserController(final AddUserToSessionService addUserToSessionService) {
    this.addUserToSessionService = addUserToSessionService;
  }

  @MessageMapping("/add-user")
  @SendTo("/topic/users")
  public NewUserResponseNotification receiveNewUserRequestNotification(
      final NewUserRequestNotification newUserRequestNotification) {
    return addUserToSessionService
        .addUserToSessionAndReturnAllUsers(newUserRequestNotification);
  }

}