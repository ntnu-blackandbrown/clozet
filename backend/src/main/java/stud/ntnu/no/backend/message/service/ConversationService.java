package stud.ntnu.no.backend.message.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stud.ntnu.no.backend.message.dto.ConversationDTO;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.mapper.MessageMapper;
import stud.ntnu.no.backend.message.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing conversations.
 * <p>
 * This service provides methods for retrieving and archiving user conversations.
 */
@Service
public class ConversationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final WebSocketService webSocketService;

    /**
     * Constructs a new ConversationService with the specified dependencies.
     *
     * @param messageRepository the MessageRepository
     * @param messageMapper the MessageMapper
     * @param webSocketService the WebSocketService
     */
    @Autowired
    public ConversationService(MessageRepository messageRepository,
                               MessageMapper messageMapper,
                               WebSocketService webSocketService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.webSocketService = webSocketService;
    }

    /**
     * Retrieves all conversations for a user.
     *
     * @param userId the ID of the user
     * @return a list of ConversationDTOs
     */
    public List<ConversationDTO> getUserConversations(String userId) {
        logger.info("Retrieving conversations for user: {}", userId);

        // Get all non-archived messages where user is sender or receiver
        List<Message> allMessages = messageRepository.findNonArchivedMessagesByUserId(userId);

        // Group by conversation (senderId + receiverId + itemId)
        Map<String, List<Message>> conversationGroups = allMessages.stream()
            .collect(Collectors.groupingBy(message ->
                generateConversationId(message.getSenderId(), message.getReceiverId(),
                                      message.getItem() != null ? message.getItem().getId() : null)));

        // Convert to ConversationDTOs
        List<ConversationDTO> conversations = new ArrayList<>();
        for (Map.Entry<String, List<Message>> entry : conversationGroups.entrySet()) {
            List<Message> messages = entry.getValue();
            // Skip empty conversations
            if (messages.isEmpty()) {
                continue;
            }

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

    /**
     * Generates a conversation ID based on sender, receiver, and item ID.
     *
     * @param senderId the sender ID
     * @param receiverId the receiver ID
     * @param itemId the item ID
     * @return the conversation ID
     */
    private String generateConversationId(String senderId, String receiverId, Long itemId) {
        // Create a consistent conversation ID regardless of who is sender/receiver
        String[] parties = {senderId, receiverId};
        java.util.Arrays.sort(parties);
        return parties[0] + "_" + parties[1] + "_" + (itemId != null ? itemId : "null");
    }

    /**
     * Archives a conversation for a user.
     *
     * @param conversationId the ID of the conversation
     * @param userId the ID of the user
     */
    public void archiveConversation(String conversationId, String userId) {
        logger.info("Archiving conversation: {} for user: {}", conversationId, userId);

        // Get all messages in this conversation
        List<Message> messages = messageRepository.findByConversationId(conversationId);
        
        // Mark each message as archived for this user
        for (Message message : messages) {
            if (message.getSenderId().equals(userId)) {
                message.setArchivedBySender(true);
            }
            if (message.getReceiverId().equals(userId)) {
                message.setArchivedByReceiver(true);
            }
            messageRepository.save(message);
        }

        // Notify via WebSocket
        webSocketService.notifyConversationArchived(conversationId, userId);
    }

    /**
     * Marks user as deleted in the conversations they participated in.
     * This adds a note to indicate that the user has been deleted.
     *
     * @param userId the ID of the deleted user
     */
    public void markUserAsDeleted(String userId) {
        logger.info("Marking user as deleted in conversations: {}", userId);
        
        // Find all conversations where this user was a participant
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(userId, userId);
        
        if (messages.isEmpty()) {
            logger.info("No messages found for user {}", userId);
            return;
        }
        
        // Group by conversation for efficiency
        Map<String, List<Message>> conversationGroups = messages.stream()
            .collect(Collectors.groupingBy(message ->
                generateConversationId(message.getSenderId(), message.getReceiverId(),
                                      message.getItem() != null ? message.getItem().getId() : null)));
        
        // Add a system message to each conversation
        for (Map.Entry<String, List<Message>> entry : conversationGroups.entrySet()) {
            String conversationId = entry.getKey();
            List<Message> conversationMessages = entry.getValue();
            
            if (conversationMessages.isEmpty()) {
                continue;
            }
            
            // Find the other participant in the conversation
            Message firstMessage = conversationMessages.get(0);
            String otherParticipantId = firstMessage.getSenderId().equals(userId) ? 
                firstMessage.getReceiverId() : firstMessage.getSenderId();
            
            // Create a system message
            Message systemMessage = new Message();
            systemMessage.setSenderId("system");
            systemMessage.setReceiverId(otherParticipantId);
            systemMessage.setItem(firstMessage.getItem());
            systemMessage.setContent("This user has deleted their account.");
            systemMessage.setCreatedAt(LocalDateTime.now());
            systemMessage.setRead(false);
            systemMessage.setArchivedBySender(true); // System messages are archived by sender by default
            
            // Save the system message
            messageRepository.save(systemMessage);
            
            // Notify via WebSocket about the new system message
            webSocketService.notifyMessageCreated(messageMapper.toDTO(systemMessage));
        }
        
        logger.info("Successfully marked user as deleted in conversations: {}", userId);
    }
}
