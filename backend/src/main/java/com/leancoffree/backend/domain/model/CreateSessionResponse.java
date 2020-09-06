package com.leancoffree.backend.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSessionResponse {
  private String id;
}
