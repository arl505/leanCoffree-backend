package com.leancoffree.backend.controller;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;

import com.leancoffree.backend.domain.model.DisplayNameAndSessionIdBody;
import com.leancoffree.backend.domain.model.ListOfDisplayNamesBody;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.service.AddUserToSessionService;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AddUserController {

  private final AddUserToSessionService addUserToSessionService;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public AddUserController(final AddUserToSessionService addUserToSessionService,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.addUserToSessionService = addUserToSessionService;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  @CrossOrigin
  @PostMapping("/add-user-to-session")
  public ResponseEntity<SuccessOrFailureAndErrorBody> addUserHttpEndpoint(
      @RequestBody final DisplayNameAndSessionIdBody displayNameAndSessionIdBody) {
    try {
      final ListOfDisplayNamesBody listOfDisplayNamesBody = addUserToSessionService
          .addUserToSessionAndReturnAllUsers(displayNameAndSessionIdBody);

      final JSONObject webSocketMessageJson = new JSONObject()
          .put("displayNames", listOfDisplayNamesBody.getDisplayNames());
      final String websocketMessageString = webSocketMessageJson.toString();
      webSocketMessagingTemplate
          .convertAndSend("/topic/session/" + displayNameAndSessionIdBody.getSessionId(),
              websocketMessageString);

      return ResponseEntity.ok(SuccessOrFailureAndErrorBody.builder()
          .status(SUCCESS)
          .build());

    } catch (AddUserToSessionException e) {
      return ResponseEntity.ok(SuccessOrFailureAndErrorBody.builder()
          .status(FAILURE)
          .error(e.getMessage())
          .build());
    }
  }
}