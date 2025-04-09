package stud.ntnu.no.backend.transaction.exception;

import stud.ntnu.no.backend.common.exception.BaseException;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a transaction validation fails.
 * <p>
 * This exception is mapped to a 400 Bad Request HTTP status code.
 */
//@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionValidationException extends BaseException {

  /**
   * Constructs a new TransactionValidationException with the specified detail message.
   *
   * @param message the detail message
   */
  public TransactionValidationException(String message) {
    super(message);
  }
}
