package stud.ntnu.no.backend.message.dto;

import java.time.LocalDateTime;

/**
 * Request object for updating a message.
 *
 * <p>This class holds information about the content and timestamp of a message to be updated.
 */
public class UpdateMessageRequest {

  private String content;
  private LocalDateTime timestamp;

  // Default constructor
  public UpdateMessageRequest() {}

  /**
   * Constructs a new UpdateMessageRequest with the specified content and timestamp.
   *
   * @param content the content of the message
   * @param timestamp the timestamp of the message
   */
  public UpdateMessageRequest(String content, LocalDateTime timestamp) {
    this.content = content;
    this.timestamp = timestamp;
  }

  /**
   * Returns the content of the message.
   *
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the content of the message.
   *
   * @param content the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Returns the timestamp of the message.
   *
   * @return the timestamp
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the timestamp of the message.
   *
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
