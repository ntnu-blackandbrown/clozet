package stud.ntnu.no.backend.message.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for a message.
 * <p>
 * This class holds information about a message, including its ID, sender, receiver, content, creation time, and read status.
 */
public class MessageDTO {
    private Long id;
    private String senderId;
    private String receiverId;
    private Long itemId;
    private String content;
    private LocalDateTime createdAt;
    private boolean isRead;

    // No-args constructor for Jackson deserialization
    public MessageDTO() {
    }
    
    /**
     * Constructs a new MessageDTO with the specified details.
     *
     * @param id the ID of the message
     * @param senderId the ID of the sender
     * @param receiverId the ID of the receiver
     * @param itemId the ID of the item associated with the message
     * @param content the content of the message
     * @param createdAt the creation time of the message
     * @param isRead the read status of the message
     */
    public MessageDTO(Long id, String senderId, String receiverId, Long itemId, String content, LocalDateTime createdAt, boolean isRead) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.itemId = itemId;
        this.content = content;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    /**
     * Returns the ID of the message.
     *
     * @return the message ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the message.
     *
     * @param id the message ID to set
     */
    public void setId(Long id) {
        this.id = id;
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
     * Returns the ID of the item associated with the message.
     *
     * @return the item ID
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * Sets the ID of the item associated with the message.
     *
     * @param itemId the item ID to set
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
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
     * Returns the creation time of the message.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation time of the message.
     *
     * @param createdAt the creation time to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the read status of the message.
     *
     * @return true if read, false otherwise
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Sets the read status of the message.
     *
     * @param read true if read, false otherwise
     */
    public void setRead(boolean read) {
        isRead = read;
    }
}