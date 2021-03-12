package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.SessionVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifySessionResponse {

  private SessionVerificationStatus verificationStatus;
  private SessionDetails sessionDetails;

}
