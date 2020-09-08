package com.leancoffree.backend.config;

import java.security.Principal;

class StompPrincipal implements Principal {

  private final String name;

  public StompPrincipal(final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}