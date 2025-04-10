package stud.ntnu.no.backend.common.exception;

/**
 * Base exception class for all application-specific exceptions.
 * <p>
 * This class serves as the parent class for all custom exceptions in the application, providing a
 * common structure and behavior. It extends RuntimeException so that all derived exceptions will be
 * unchecked exceptions, allowing for more flexible exception handling throughout the application.
 * </p>
 * <p>
 * All exception classes in the application should extend this class to ensure consistent exception
 * handling and error reporting. This allows for uniform exception handling strategies and
 * centralized error processing.
 * </p>
 *
 * @see RuntimeException
 */
public class BaseException extends RuntimeException {

  /**
   * Constructs a new BaseException with the specified error message.
   * <p>
   * The message should be a clear and descriptive explanation of the error that occurred, providing
   * enough context to understand the cause of the exception.
   * </p>
   *
   * @param message the detailed error message
   */
  public BaseException(String message) {
    super(message);
  }
}
