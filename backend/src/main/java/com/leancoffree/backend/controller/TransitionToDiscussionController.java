package com.leancoffree.backend.controller;

import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.service.TransitionToDiscussionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransitionToDiscussionController {

  private final TransitionToDiscussionService transitionToDiscussionService;

  public TransitionToDiscussionController(
      final TransitionToDiscussionService transitionToDiscussionService) {
    this.transitionToDiscussionService = transitionToDiscussionService;
  }

  @CrossOrigin
  @PostMapping("/transition-to-discussion/{sessionId}")
  public ResponseEntity<SuccessOrFailureAndErrorBody> transitionToDiscussionEndpoint(
      @PathVariable("sessionId") final String sessionId) {
    return ResponseEntity
        .ok(transitionToDiscussionService.transitionToDiscussion(sessionId));
  }
}
