package com.example.demo.exception;

public class DuplicateAccountIdException extends RuntimeException {

  public DuplicateAccountIdException(String message) {
    super(message);
  }
}
