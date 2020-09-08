package com.leancoffree.backend.controller;

public class RefreshUsersInSessionException extends Exception {

  public RefreshUsersInSessionException(final String errorMessage) {
    super(errorMessage);
  }
}
