package stud.ntnu.no.backend.message.dto;

import java.time.LocalDateTime;

/**
 * Request object for creating a new message.
 *
 * <p>This class holds information about the sender, receiver, content, and timestamp of a message
 * to be created.
 */
public class CreateMessageRequest {

  private String senderId;
  private String receiverId;
  private String content;
  private LocalDateTime timestamp;

  // Default constructor
  public CreateMessageRequest() {}

  /**
   * Constructs a new CreateMessageRequest with the specified details.
   *
   * @param senderId the ID of the sender
   * @param receiverId the ID of the receiver
   * @param content the content of the message
   * @param timestamp the timestamp of the message
   */
  // Constructor with arguments
  public CreateMessageRequest(
      String senderId, String receiverId, String content, LocalDateTime timestamp) {
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.content = content;
    this.timestamp = timestamp;
  }

  /**
   * Returns the ID of the sender.
   *
   * @return the sender ID
   */
  public String getSenderId() {
    return senderId;
  }

  /**
   * Sets the ID of the sender.
   *
   * @param senderId the sender ID to set
   */
  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  /**
   * Returns the ID of the receiver.
   *
   * @return the receiver ID
   */
  public String getReceiverId() {
    return receiverId;
  }

  /**
   * Sets the ID of the receiver.
   *
   * @param receiverId the receiver ID to set
   */
  public void setReceiverId(String receiverId) {
    this.receiverId = receiverId;
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
