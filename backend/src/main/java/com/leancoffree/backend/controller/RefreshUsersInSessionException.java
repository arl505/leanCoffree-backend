package com.leancoffree.backend.controller;

public class RefreshUsersInSessionException extends Exception {

  public RefreshUsersInSessionException(String errorMessage) {
    super(errorMessage);
  }
}