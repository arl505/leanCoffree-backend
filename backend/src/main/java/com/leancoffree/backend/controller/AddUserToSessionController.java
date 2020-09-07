package com.leancoffree.backend.controller;

import com.leancoffree.backend.domain.model.AddUserToSessionRequest;
import com.leancoffree.backend.domain.model.AddUserToSessionResponse;
import com.leancoffree.backend.service.AddUserToSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddUserToSessionController {

  private final AddUserToSessionService addUserToSessionService;

  public AddUserToSessionController(final AddUserToSessionService addUserToSessionSerivce) {
    this.addUserToSessionService = addUserToSessionSerivce;
  }

  @CrossOrigin
  @PostMapping("/add-user-to-session")
  public ResponseEntity<AddUserToSessionResponse> addUserToSessionEndpoint(
      @RequestBody AddUserToSessionRequest addUserToSessionRequest) {
    return ResponseEntity.ok(addUserToSessionService.addUserToSession(addUserToSessionRequest));
  }
}
