package stud.ntnu.no.backend.category.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when category validation fails. Returns HTTP 400 Bad Request response to the
 * client.
 */
public class CategoryValidationException extends BaseException {

  private static final Logger logger = LoggerFactory.getLogger(CategoryValidationException.class);

  /**
   * Constructs the exception with a message describing the validation error.
   *
   * @param message Description of the validation error
   */
  public CategoryValidationException(String message) {
    super(message);
    logger.warn("CategoryValidationException thrown: {}", message);
  }
}
