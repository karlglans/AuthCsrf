package se.plushogskolan.service.exceptions;

public class BadCredentialsException extends ServiceException {
  public BadCredentialsException(String message) {
    super(message);
  }
}
