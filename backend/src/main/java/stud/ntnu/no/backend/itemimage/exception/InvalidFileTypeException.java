package stud.ntnu.no.backend.itemimage.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/** Exception thrown when a file type is not supported. */
public class InvalidFileTypeException extends BaseException {

  /**
   * Constructs a new InvalidFileTypeException with the specified detail message.
   *
   * @param message The detail message explaining the exception
   */
  public InvalidFileTypeException(String message) {
    super(message);
  }
}
