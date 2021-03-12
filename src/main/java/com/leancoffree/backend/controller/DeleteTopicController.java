package com.leancoffree.backend.controller;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;

import com.leancoffree.backend.domain.model.DeleteTopicRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.service.DeleteTopicService;
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
public class DeleteTopicController {

  private final DeleteTopicService deleteTopicService;

  public DeleteTopicController(final DeleteTopicService deleteTopicService) {
    this.deleteTopicService = deleteTopicService;
  }

  @CrossOrigin
  @PostMapping("/delete-topic")
  public ResponseEntity<SuccessOrFailureAndErrorBody> deleteTopicEndpoint(
      @Valid @RequestBody final DeleteTopicRequest deleteTopicRequest, final Errors errors) {

    if (errors.hasErrors()) {
      return buildValidationErrorsResponse(errors);
    }
    return ResponseEntity.ok(deleteTopicService.deleteTopic(deleteTopicRequest));
  }

  private ResponseEntity<SuccessOrFailureAndErrorBody> buildValidationErrorsResponse(
      final Errors errors) {
    final List<String> errorsList = new ArrayList<>();
    for (final ObjectError error : errors.getAllErrors()) {
      errorsList.add(error.getDefaultMessage());
    }
    return ResponseEntity
        .ok(new SuccessOrFailureAndErrorBody(FAILURE, String.join(", ", errorsList)));
  }
}
