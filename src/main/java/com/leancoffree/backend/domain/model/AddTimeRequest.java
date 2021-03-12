package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.AddTimeIncrement;
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
public class AddTimeRequest {

  @Size(min = 1, max = 100, message = "sessionId should be between 1-100 chars")
  @NotBlank(message = "sessionId should not be null")
  private String sessionId;

  @NotNull(message = "increment should not be null")
  private AddTimeIncrement increment;
}
