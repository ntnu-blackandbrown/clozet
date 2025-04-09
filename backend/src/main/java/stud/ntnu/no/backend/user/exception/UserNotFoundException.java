package stud.ntnu.no.backend.user.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a requested user is not found in the system.
 *
 * <p>This exception is thrown when an operation attempts to access or manipulate a user that does
 * not exist in the database, such as when retrieving a user by ID or username that does not match
 * any existing record.
 *
 * <p>In REST API responses, this exception typically results in a 404 Not Found HTTP status code.
 */
public class UserNotFoundException extends BaseException {

  /**
   * Constructs a new UserNotFoundException with a message indicating the user ID that could not be
   * found.
   *
   * @param id the ID of the user that was not found
   */
  public UserNotFoundException(Long id) {
    super("User not found with id: " + id);
  }

  /**
   * Constructs a new UserNotFoundException with the specified detail message.
   *
   * <p>This constructor allows for more specific error messages, such as when searching for a user
   * by username or email.
   *
   * @param message the detail message explaining which user was not found
   */
  public UserNotFoundException(String message) {
    super(message);
  }
}
