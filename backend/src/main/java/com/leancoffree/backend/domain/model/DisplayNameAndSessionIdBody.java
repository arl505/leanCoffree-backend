package com.leancoffree.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisplayNameAndSessionIdBody {

  // adding session ID for now
  // todo: remove session id by using sessionId as param in websocket path
  private String sessionId;
  private String displayName;
}
