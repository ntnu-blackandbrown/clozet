package stud.ntnu.no.backend.message.service;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.message.dto.MessageDTO;

/**
 * Service for WebSocket communication.
 * <p>
 * This service provides methods for broadcasting message and conversation events via WebSocket.
 */
@Service
public class WebSocketService {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
  private final SimpMessagingTemplate messagingTemplate;

  @Autowired
  public WebSocketService(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Constructs a new WebSocketService with the specified messaging template.
   *
   * @param messagingTemplate the SimpMessagingTemplate
   */
  public void notifyMessageCreated(MessageDTO message) {
    try {
      logger.info("Broadcasting new message with ID: {}", message.getId());
      messagingTemplate.convertAndSend("/topic/messages", message);

      // Also notify specific user's topic
      String userDestination = "/topic/user." + message.getReceiverId();
      messagingTemplate.convertAndSend(userDestination, message);
    } catch (Exception e) {
      logger.error("Error broadcasting message: {}", e.getMessage(), e);
    }
  }

  /**
   * Notifies clients of a newly created message.
   *
   * @param message the MessageDTO
   */
  public void notifyMessageRead(MessageDTO message) {
    try {
      logger.info("Broadcasting message marked as read with ID: {}", message.getId());
      messagingTemplate.convertAndSend("/topic/messages.read", message);
    } catch (Exception e) {
      logger.error("Error broadcasting message read status: {}", e.getMessage(), e);
    }
  }

  /**
   * Notifies clients of a message update.
   *
   * @param message the MessageDTO
   */
  public void notifyMessageUpdated(MessageDTO message) {
    try {
      logger.info("Broadcasting message update with ID: {}", message.getId());
      messagingTemplate.convertAndSend("/topic/messages.update", message);
    } catch (Exception e) {
      logger.error("Error broadcasting message update: {}", e.getMessage(), e);
    }
  }

  /**
   * Notifies clients of a message deletion.
   *
   * @param messageId the ID of the deleted message
   */
  public void notifyMessageDeleted(Long messageId) {
    try {
      logger.info("Broadcasting message deletion with ID: {}", messageId);
      messagingTemplate.convertAndSend("/topic/messages.delete", messageId);
    } catch (Exception e) {
      logger.error("Error broadcasting message deletion: {}", e.getMessage(), e);
    }
  }

  // Add these methods to your existing WebSocketService class

  /**
   * Notifies clients that a conversation has been archived.
   *
   * @param conversationId the ID of the conversation
   * @param userId         the ID of the user
   */
  public void notifyConversationArchived(String conversationId, String userId) {
    try {
      logger.info("Broadcasting conversation archive: {}", conversationId);
      messagingTemplate.convertAndSend("/topic/conversations.archive",
          Map.of("conversationId", conversationId, "userId", userId));
    } catch (Exception e) {
      logger.error("Error broadcasting conversation archive: {}", e.getMessage(), e);
    }
  }

  /**
   * Notifies clients of a conversation deletion.
   *
   * @param conversationId the ID of the deleted conversation
   */
  public void notifyConversationDeleted(String conversationId) {
    try {
      logger.info("Broadcasting conversation deletion: {}", conversationId);
      messagingTemplate.convertAndSend("/topic/conversations.delete", conversationId);
    } catch (Exception e) {
      logger.error("Error broadcasting conversation deletion: {}", e.getMessage(), e);
    }
  }
}