package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.VoteType;
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
public class PostVoteRequest {

  @Size(min = 1, max = 100, message = "sessionId should be between 1-100 chars")
  @NotBlank(message = "sessionId should not be null")
  private String sessionId;

  @Size(min = 1, max = 500, message = "submissionText should be between 1-500 chars")
  @NotBlank(message = "submissionText should not be null")
  private String text;

  @Size(min = 1, max = 20, message = "voterDisplayName should be between 1-20 chars")
  @NotBlank(message = "voterDisplayName should not be null")
  private String voterDisplayName;

  @Size(min = 1, max = 20, message = "authorDisplayName should be between 1-20 chars")
  @NotBlank(message = "authorDisplayName should not be null")
  private String authorDisplayName;

  @NotNull(message = "command should not be null")
  private VoteType command;
}
