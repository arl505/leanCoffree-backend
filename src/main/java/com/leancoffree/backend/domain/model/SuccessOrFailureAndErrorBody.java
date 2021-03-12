package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.SuccessOrFailure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessOrFailureAndErrorBody {

  private SuccessOrFailure status;
  private String error;
}
