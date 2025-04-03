package stud.ntnu.no.backend.message.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.message.dto.ConversationDTO;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.mapper.MessageMapper;
import stud.ntnu.no.backend.message.repository.MessageRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConversationService {
    
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final WebSocketService webSocketService;
    
    @Autowired
    public ConversationService(MessageRepository messageRepository, 
                               MessageMapper messageMapper,
                               WebSocketService webSocketService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.webSocketService = webSocketService;
    }
    
    public List<ConversationDTO> getUserConversations(String userId) {
        // Get all messages where user is sender or receiver
        List<Message> allMessages = messageRepository.findBySenderIdOrReceiverId(userId, userId);
        
        // Group by conversation (senderId + receiverId + itemId)
        Map<String, List<Message>> conversationGroups = allMessages.stream()
            .collect(Collectors.groupingBy(message -> 
                generateConversationId(message.getSenderId(), message.getReceiverId(), 
                                      message.getItem() != null ? message.getItem().getId() : null)));
        
        // Convert to ConversationDTOs
        List<ConversationDTO> conversations = new ArrayList<>();
        for (Map.Entry<String, List<Message>> entry : conversationGroups.entrySet()) {
            List<Message> messages = entry.getValue();
            // Sort messages by timestamp
            messages.sort(Comparator.comparing(Message::getCreatedAt));
            
            // Get latest message for summary info
            Message latestMessage = messages.get(messages.size() - 1);
            
            // Create ConversationDTO
            ConversationDTO conversation = new ConversationDTO(
                entry.getKey(),
                messages.get(0).getSenderId(),
                messages.get(0).getReceiverId(),
                messages.get(0).getItem() != null ? messages.get(0).getItem().getId() : null,
                latestMessage.getContent(),
                latestMessage.getCreatedAt(),
                false, // Default to not archived
                messages.stream().map(messageMapper::toDTO).collect(Collectors.toList())
            );
            
            conversations.add(conversation);
        }
        
        // Sort conversations by last message timestamp
        conversations.sort(Comparator.comparing(ConversationDTO::getLastMessageTime).reversed());
        
        return conversations;
    }
    
    private String generateConversationId(String senderId, String receiverId, Long itemId) {
        // Create a consistent conversation ID regardless of who is sender/receiver
        String[] parties = {senderId, receiverId};
        java.util.Arrays.sort(parties);
        return parties[0] + "_" + parties[1] + "_" + (itemId != null ? itemId : "null");
    }
    
    public void archiveConversation(String conversationId, String userId) {
        // Implementation would depend on how you want to store archived status
        // For now, just notify via WebSocket
        webSocketService.notifyConversationArchived(conversationId, userId);
    }
}