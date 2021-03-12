package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.RefreshUsersCommand;
import javax.validation.constraints.NotEmpty;
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
public class RefreshUsersRequest {

  @NotNull(message = "command should not be null")
  private RefreshUsersCommand command;

  @Size(min = 1, max = 20, message = "displayName should be between 1-20 chars")
  @NotEmpty(message = "displayName should not be empty")
  private String displayName;

  @Size(min = 1, max = 100, message = "sessionId should be between 1-100 chars")
  @NotEmpty(message = "sessionId should not be empty")
  private String sessionId;

  @Size(min = 1, max = 100, message = "websocketUserId should be between 1-100 chars")
  @NotEmpty(message = "websocketUserId should not be empty")
  private String websocketUserId;

}
