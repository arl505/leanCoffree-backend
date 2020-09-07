package com.leancoffree.backend.domain.model;

import lombok.Data;

@Data
public class AddUserToSessionRequest {

  private String sessionId;
  private String displayName;
}
