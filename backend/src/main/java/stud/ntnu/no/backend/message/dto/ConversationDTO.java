package stud.ntnu.no.backend.message.dto;

import java.time.LocalDateTime;
import java.util.List;

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
    // Add all getters and setters here


    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}