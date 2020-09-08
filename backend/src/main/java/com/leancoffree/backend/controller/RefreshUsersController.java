package com.leancoffree.backend.controller;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;
import static com.leancoffree.backend.enums.SuccessOrFailure.SUCCESS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.leancoffree.backend.domain.model.ListOfDisplayNamesBody;
import com.leancoffree.backend.domain.model.RefreshUsersRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.service.RefreshUsersInSessionService;
import javax.validation.Valid;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RefreshUsersController {

  private final RefreshUsersInSessionService refreshUsersInSessionService;
  private final SimpMessagingTemplate webSocketMessagingTemplate;

  public RefreshUsersController(final RefreshUsersInSessionService refreshUsersInSessionService,
      final SimpMessagingTemplate webSocketMessagingTemplate) {
    this.refreshUsersInSessionService = refreshUsersInSessionService;
    this.webSocketMessagingTemplate = webSocketMessagingTemplate;
  }

  @CrossOrigin
  @PostMapping("/refresh-users")
  public ResponseEntity<Object> addUserHttpEndpoint(
      @Valid @RequestBody final RefreshUsersRequest refreshUsersRequest, final Errors errors) {

    if (errors.hasErrors()) {
      return buildValidationErrorsResponse(errors);
    }

    try {
      final ListOfDisplayNamesBody listOfDisplayNamesBody = refreshUsersInSessionService
          .refreshUsersInSession(refreshUsersRequest);

      final JSONObject webSocketMessageJson = new JSONObject()
          .put("displayNames", listOfDisplayNamesBody.getDisplayNames());
      final String websocketMessageString = webSocketMessageJson.toString();
      webSocketMessagingTemplate
          .convertAndSend("/topic/session/" + refreshUsersRequest.getSessionId(),
              websocketMessageString);

      return ResponseEntity.ok(SuccessOrFailureAndErrorBody.builder()
          .status(SUCCESS)
          .build());

    } catch (RefreshUsersInSessionException e) {
      return ResponseEntity.ok(SuccessOrFailureAndErrorBody.builder()
          .status(FAILURE)
          .error(e.getMessage())
          .build());
    }
  }

  private ResponseEntity<Object> buildValidationErrorsResponse(final Errors errors) {
    final JSONArray errorsJsonArray = new JSONArray();
    for(final ObjectError error : errors.getAllErrors()) {
      errorsJsonArray.put(error.getDefaultMessage());
    }
    final String errorsJsonString = new JSONObject()
        .put("errors", errorsJsonArray).toString();
    return new ResponseEntity<>(errorsJsonString, BAD_REQUEST);
  }

}