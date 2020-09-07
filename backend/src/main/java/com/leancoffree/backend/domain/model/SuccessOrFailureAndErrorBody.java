package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.SuccessOrFailure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuccessOrFailureAndErrorBody {

  private SuccessOrFailure status;
  private String error;
}
