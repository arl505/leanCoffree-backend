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
public class AddUserResponse extends SuccessOrFailureAndErrorBody {

  private SessionStatus sessionStatus;
  private Boolean showShareableLink;

  @Builder
  public AddUserResponse(final Boolean showShareableLink, final SessionStatus sessionStatus,
      final SuccessOrFailure status, final String error) {
    super(status, error);
    this.sessionStatus = sessionStatus;
    this.showShareableLink = showShareableLink;
  }
}
