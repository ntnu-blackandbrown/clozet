package stud.ntnu.no.backend.message.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a message is not found.
 * <p>
 * This exception is mapped to a 404 Not Found HTTP status code.
 */

public class MessageNotFoundException extends BaseException {

  /**
   * Constructs a new MessageNotFoundException with the specified message ID.
   *
   * @param id the ID of the message that was not found
   */
  public MessageNotFoundException(Long id) {
    super("Message not found with id: " + id);
  }
}
