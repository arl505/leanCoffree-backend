package com.leancoffree.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserResponseNotification {

  private String addOrDrop;
  private String displayName;
}