package stud.ntnu.no.backend.location.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a location validation fails.
 * <p>
 * This exception is mapped to a 400 Bad Request HTTP status code.
 */
public class LocationValidationException extends BaseException {

  /**
   * Constructs a new LocationValidationException with the specified detail message.
   *
   * @param message the detail message
   */
  public LocationValidationException(String message) {
    super(message);
  }
}
