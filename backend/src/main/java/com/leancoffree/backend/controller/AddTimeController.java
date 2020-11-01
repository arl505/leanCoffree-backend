package com.leancoffree.backend.controller;

import static com.leancoffree.backend.enums.SuccessOrFailure.FAILURE;

import com.leancoffree.backend.domain.model.AddTimeRequest;
import com.leancoffree.backend.domain.model.SuccessOrFailureAndErrorBody;
import com.leancoffree.backend.service.AddTimeService;
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
public class AddTimeController {

  private final AddTimeService addTimeService;

  public AddTimeController(final AddTimeService addTimeService) {
    this.addTimeService = addTimeService;
  }

  @CrossOrigin
  @PostMapping("/add-time")
  public ResponseEntity<SuccessOrFailureAndErrorBody> addTimeEndpoint(
      @Valid @RequestBody final AddTimeRequest addTimeRequest, final Errors errors) {

    if (errors.hasErrors()) {
      return buildValidationErrorsResponse(errors);
    }
    return ResponseEntity.ok(addTimeService.addTime(addTimeRequest));
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
