package stud.ntnu.no.backend.itemimage.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/** Exception thrown when an attempt is made to process an empty file. */
public class EmptyFileException extends BaseException {

  /**
   * Constructs a new EmptyFileException with the specified detail message.
   *
   * @param message The detail message explaining the exception
   */
  public EmptyFileException(String message) {
    super(message);
  }
}
