package com.urlshortner.urlshortner.exception;

public class QrCodeGenerationException extends RuntimeException {
  public QrCodeGenerationException(String message) {
    super(message);
  }
}
