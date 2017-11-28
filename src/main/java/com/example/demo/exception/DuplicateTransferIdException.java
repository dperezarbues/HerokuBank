package com.example.demo.exception;

public class DuplicateTransferIdException extends RuntimeException {
  public DuplicateTransferIdException(String message) {
    super(message);
  }
}
