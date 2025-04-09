package stud.ntnu.no.backend.history.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a requested history record is not found in the system.
 *
 * <p>This exception is thrown when an operation attempts to retrieve, update, or delete a history
 * record that does not exist in the database.
 *
 * <p>In REST API responses, this exception typically results in a 404 Not Found HTTP status code.
 */
public class HistoryNotFoundException extends BaseException {

  /**
   * Constructs a new HistoryNotFoundException with the specified detail message.
   *
   * <p>The message should provide information about which history record could not be found and, if
   * relevant, how it was being searched for (ID, user, etc.).
   *
   * @param message the detail message explaining which history record was not found
   */
  public HistoryNotFoundException(String message) {
    super(message);
  }

  /**
   * Constructs a new HistoryNotFoundException with a message indicating the history ID that could
   * not be found.
   *
   * @param id the ID of the history record that was not found
   */
  public HistoryNotFoundException(Long id) {
    super("History record not found with id: " + id);
  }
}
