package com.leancoffree.backend.controller;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;

import com.leancoffree.backend.domain.model.SubmitTopicRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.service.SubmitTopicService;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubmitTopicController {

  private final SubmitTopicService submitTopicService;

  public SubmitTopicController(final SubmitTopicService submitTopicService) {
    this.submitTopicService = submitTopicService;
  }

  @CrossOrigin
  @PostMapping("/submit-topic")
  public ResponseEntity<SuccessOrFailureAndErrorBody> submitTopicEndpoint(
      @Valid @RequestBody final SubmitTopicRequest submitTopicRequest, final Errors errors) {

    if (errors.hasErrors()) {
      return buildValidationErrorsResponse(errors);
    }
    return ResponseEntity.ok(submitTopicService.submitTopic(submitTopicRequest));
  }

  private ResponseEntity<SuccessOrFailureAndErrorBody> buildValidationErrorsResponse(
      final Errors errors) {
    final List<String> errorsList = new ArrayList<>();
    for (final ObjectError error : errors.getAllErrors()) {
      errorsList.add(error.getDefaultMessage());
    }
    return ResponseEntity.status(400).body(SuccessOrFailureAndErrorBody.builder()
        .status(FAILURE)
        .error(String.join(", ", errorsList))
        .build());
  }
}
