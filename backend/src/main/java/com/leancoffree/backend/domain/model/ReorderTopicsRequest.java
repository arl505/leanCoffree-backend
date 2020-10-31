package com.leancoffree.backend.domain.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReorderTopicsRequest {

  @Size(min = 1, max = 100, message = "sessionId should be between 1-500 chars")
  @NotBlank(message = "sessionId should not be blank")
  private String sessionId;

  @Size(min = 1, max = 500, message = "submissionText should be between 1-500 chars")
  @NotBlank(message = "submissionText should not be null")
  private String text;

  @NotNull(message = "newIndex should not be null")
  private Integer newIndex;
}
