package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.RefreshUsersCommand;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

  @NotEmpty(message = "displayName should not be empty")
  private String displayName;

  @NotEmpty(message = "sessionId should not be empty")
  private String sessionId;

}
