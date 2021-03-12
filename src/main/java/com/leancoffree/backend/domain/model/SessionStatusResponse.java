package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.SessionStatus;
import com.leancoffree.backend.enums.SuccessOrFailure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionStatusResponse extends SuccessOrFailureAndErrorBody {

  private SessionStatus sessionStatus;

  @Builder
  public SessionStatusResponse(final SessionStatus sessionStatus, final SuccessOrFailure status,
      final String error) {
    super(status, error);
    this.sessionStatus = sessionStatus;
  }
}
