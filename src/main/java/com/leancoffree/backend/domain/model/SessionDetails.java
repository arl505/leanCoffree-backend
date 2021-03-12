package com.leancoffree.backend.domain.model;

import com.leancoffree.backend.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionDetails {

  private String sessionId;
  private SessionStatus sessionStatus;

}
