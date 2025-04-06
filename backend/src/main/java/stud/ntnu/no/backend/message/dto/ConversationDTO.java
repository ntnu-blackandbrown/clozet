package stud.ntnu.no.backend.message.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for a conversation.
 * <p>
 * This class holds information about a conversation, including its ID, participants,
 * last message, and a list of messages.
 */
public class ConversationDTO {
    private String conversationId; // Could be composite: senderId_receiverId_itemId
    private String senderId;
    private String receiverId;
    private Long itemId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean archived;
    private List<MessageDTO> messages;

    // Constructors
    public ConversationDTO() {
    }

    /**
     * Constructs a new ConversationDTO with the specified details.
     *
     * @param conversationId the ID of the conversation
     * @param senderId the ID of the sender
     * @param receiverId the ID of the receiver
     * @param itemId the ID of the item
     * @param lastMessage the last message in the conversation
     * @param lastMessageTime the time of the last message
     * @param archived whether the conversation is archived
     * @param messages the list of messages in the conversation
     */
    public ConversationDTO(String conversationId, String senderId, String receiverId, 
                           Long itemId, String lastMessage, LocalDateTime lastMessageTime, 
                           boolean archived, List<MessageDTO> messages) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.itemId = itemId;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.archived = archived;
        this.messages = messages;
    }

    // Getters and setters
    /**
     * Returns the ID of the conversation.
     *
     * @return the conversation ID
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * Sets the ID of the conversation.
     *
     * @param conversationId the conversation ID to set
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
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
     * Returns the ID of the item.
     *
     * @return the item ID
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * Sets the ID of the item.
     *
     * @param itemId the item ID to set
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    /**
     * Returns the last message in the conversation.
     *
     * @return the last message
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * Sets the last message in the conversation.
     *
     * @param lastMessage the last message to set
     */
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    /**
     * Returns the time of the last message.
     *
     * @return the last message time
     */
    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    /**
     * Sets the time of the last message.
     *
     * @param lastMessageTime the last message time to set
     */
    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    /**
     * Returns whether the conversation is archived.
     *
     * @return true if archived, false otherwise
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * Sets whether the conversation is archived.
     *
     * @param archived true to archive, false otherwise
     */
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    /**
     * Returns the list of messages in the conversation.
     *
     * @return the list of messages
     */
    public List<MessageDTO> getMessages() {
        return messages;
    }

    /**
     * Sets the list of messages in the conversation.
     *
     * @param messages the list of messages to set
     */
    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}