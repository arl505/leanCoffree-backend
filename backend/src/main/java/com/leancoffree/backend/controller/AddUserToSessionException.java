package com.leancoffree.backend.controller;

public class AddUserToSessionException extends Exception {

  public AddUserToSessionException(String errorMessage) {
    super(errorMessage);
  }
}