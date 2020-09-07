package com.leancoffree.backend.controller;

import com.leancoffree.backend.service.StartSessionService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StartSessionController {

  private final StartSessionService startSessionService;

  public StartSessionController(final StartSessionService startSessionService) {
    this.startSessionService = startSessionService;
  }

  @CrossOrigin
  @PostMapping("/start-session/{sessionId}")
  public void startSessionEndpoint(@PathVariable("sessionId") final String sessionId) {
    startSessionService.startSession(sessionId);
  }
}
