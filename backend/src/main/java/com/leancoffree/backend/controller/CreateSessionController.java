package com.leancoffree.backend.controller;

import com.leancoffree.backend.service.CreateSessionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class CreateSessionController {

  private final CreateSessionService createSessionService;

  public CreateSessionController(final CreateSessionService createSessionService) {
    this.createSessionService = createSessionService;
  }

  @PostMapping
  public ModelAndView createSessionEndpoint() {
    return new ModelAndView(createSessionService.createSession());
  }

}
