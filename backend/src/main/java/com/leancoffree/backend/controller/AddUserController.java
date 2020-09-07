package com.leancoffree.backend.controller;

import com.leancoffree.backend.domain.model.NewUserRequestNotification;
import com.leancoffree.backend.domain.model.NewUserResponseNotification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class AddUserController {

  @MessageMapping("/add-user")
  @SendTo("/topic/users")
  public NewUserResponseNotification receiveNewUserRequestNotification(
      NewUserRequestNotification newUserRequestNotification) {
    return new NewUserResponseNotification("ADD", newUserRequestNotification.getDisplayName());
  }

}