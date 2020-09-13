package com.leancoffree.backend.domain.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitTopicRequest {

  @NotBlank(message = "sessionId should not be null")
  private String sessionId;

  @NotBlank(message = "submissionText should not be null")
  private String submissionText;
}
