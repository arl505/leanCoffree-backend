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
public class DeleteTopicRequest {

  @Size(min = 1, max = 100, message = "sessionId should be between 1-500 chars")
  @NotBlank(message = "sessionId should not be blank")
  private String sessionId;

  @Size(min = 1, max = 500, message = "topicText should be between 1-500 chars")
  @NotBlank(message = "topicText should not be null")
  private String topicText;

  @Size(min = 1, max = 20, message = "authorName should be between 1-20 chars")
  @NotNull(message = "authorName should not be null")
  private String authorName;
}
