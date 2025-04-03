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
}