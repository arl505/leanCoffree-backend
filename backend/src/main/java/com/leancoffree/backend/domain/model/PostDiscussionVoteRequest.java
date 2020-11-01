package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.DiscussionVoteType;
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
public class PostDiscussionVoteRequest {

  @Size(min = 1, max = 100, message = "sessionId should be between 1-100 chars")
  @NotBlank(message = "sessionId should not be null")
  private String sessionId;

  @Size(min = 1, max = 20, message = "userDisplayName should be between 1-20 chars")
  @NotBlank(message = "userDisplayName should not be null")
  private String userDisplayName;

  @NotNull(message = "voteType should not be null")
  private DiscussionVoteType voteType;
}
