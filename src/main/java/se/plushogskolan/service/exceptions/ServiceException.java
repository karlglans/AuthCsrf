package se.plushogskolan.service.exceptions;

public class ServiceException extends RuntimeException {
  public ServiceException(String message) {
    super(message);
  }
}
