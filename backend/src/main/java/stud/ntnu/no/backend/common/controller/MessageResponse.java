package stud.ntnu.no.backend.common.controller;

/** Represents a response containing a message. */
public class MessageResponse {

  private String message;

  /**
   * Constructs a {@code MessageResponse} with the specified message.
   *
   * @param message the message to include in the response
   */
  public MessageResponse(String message) {
    this.message = message;
  }

  /**
   * Returns the message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message.
   *
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }
}
