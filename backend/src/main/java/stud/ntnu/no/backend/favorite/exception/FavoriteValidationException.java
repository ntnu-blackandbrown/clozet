package stud.ntnu.no.backend.favorite.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when there is a validation error related to favorites. Responds with HTTP status
 * BAD_REQUEST.
 */
public class FavoriteValidationException extends BaseException {

  /**
   * Constructs a new FavoriteValidationException with the specified detail message.
   *
   * @param message The detail message explaining the validation error
   */
  public FavoriteValidationException(String message) {
    super(message);
  }
}
