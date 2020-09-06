package com.leancoffree.backend.controller;

import com.leancoffree.backend.domain.model.CreateSessionResponse;
import com.leancoffree.backend.service.CreateSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateSessionController {

  private final CreateSessionService createSessionService;

  public CreateSessionController(final CreateSessionService createSessionService) {
    this.createSessionService = createSessionService;
  }

  @PostMapping("/create-session")
  public ResponseEntity<CreateSessionResponse> createSessionEndpoint() {
    return ResponseEntity.ok(createSessionService.createSession());
  }

}
