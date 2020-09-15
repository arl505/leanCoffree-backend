package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.RefreshTopicsCommand;
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
public class RefreshTopicsRequest {

  @NotNull(message = "command should not be null")
  private RefreshTopicsCommand command;

  @Size(min = 1, max = 100, message = "sessionId should be between 1-500 chars")
  @NotBlank(message = "sessionId should not be blank")
  private String sessionId;

  @Size(min = 1, max = 500, message = "currentTopicText should be between 1-500 chars")
  @NotBlank(message = "currentTopicText should not be blank")
  private String currentTopicText;

  private String nextTopicText;
}
