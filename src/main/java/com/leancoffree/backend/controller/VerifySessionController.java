package com.leancoffree.backend.controller;

import com.leancoffree.backend.domain.model.VerifySessionResponse;
import com.leancoffree.backend.service.VerifySessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerifySessionController {

  private final VerifySessionService verifySessionService;

  public VerifySessionController(final VerifySessionService verifySessionService) {
    this.verifySessionService = verifySessionService;
  }

  @CrossOrigin
  @PostMapping("/verify-session/{sessionId}")
  public ResponseEntity<VerifySessionResponse> verifySessionEndpoint(
      @PathVariable("sessionId") final String sessionId) {
    return ResponseEntity.ok(verifySessionService.verifySession(sessionId));
  }
}
