package com.leancoffree.backend.controller;

import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.service.EndSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EndSessionController {

  private final EndSessionService endSessionService;

  public EndSessionController(final EndSessionService endSessionService) {
    this.endSessionService = endSessionService;
  }

  @CrossOrigin
  @PostMapping("/end-session/{sessionId}")
  public ResponseEntity<SuccessOrFailureAndErrorBody> endSessionEndpoint(
      @PathVariable("sessionId") final String sessionId) {
    return ResponseEntity.ok(endSessionService.endSession(sessionId));
  }

}
