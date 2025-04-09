package stud.ntnu.no.backend.transaction.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a transaction is not found.
 *
 * <p>This exception is mapped to a 404 Not Found HTTP status code.
 */
public class TransactionNotFoundException extends BaseException {

  /**
   * Constructs a new TransactionNotFoundException with the specified transaction ID.
   *
   * @param id the ID of the transaction that was not found
   */
  public TransactionNotFoundException(Long id) {
    super("Transaction not found with id: " + id);
  }

  /**
   * Constructs a new TransactionNotFoundException with the specified detail message.
   *
   * @param message the detail message
   */
  public TransactionNotFoundException(String message) {
    super(message);
  }
}
