package stud.ntnu.no.backend.user.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when user validation fails.
 * <p>
 * This exception is thrown when user data fails validation checks, such as invalid email format,
 * missing required fields, or other constraint violations. It indicates that a user-related
 * operation could not be completed due to invalid input data.
 * </p>
 * <p>
 * In REST API responses, this exception typically results in a 400 Bad Request HTTP status code.
 * </p>
 */
public class UserValidationException extends BaseException {

  /**
   * Constructs a new UserValidationException with the specified detail message.
   * <p>
   * The message should describe which validation constraint was violated and, if possible, how to
   * fix the issue.
   * </p>
   *
   * @param message the detail message explaining which validation failed
   */
  public UserValidationException(String message) {
    super(message);
  }
}
