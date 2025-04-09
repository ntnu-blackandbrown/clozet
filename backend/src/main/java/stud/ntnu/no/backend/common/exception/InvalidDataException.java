package stud.ntnu.no.backend.common.exception;

/** Exception thrown when invalid data is encountered. */
public class InvalidDataException extends BaseException {

  /**
   * Constructs an {@code InvalidDataException} with the specified message.
   *
   * @param message the detail message
   */
  public InvalidDataException(String message) {
    super(message);
  }
}
