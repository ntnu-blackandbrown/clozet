package stud.ntnu.no.backend.message.controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.service.MessageService;

/**
 * WebSocket controller for handling chat messages.
 * <p>
 * This controller provides endpoints for sending and marking messages as read via WebSocket.
 */
@Controller
public class WebSocketMessageController {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageController.class);

  private final MessageService messageService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * Constructs a new WebSocketMessageController with the specified service and messaging template.
   *
   * @param messageService    the MessageService
   * @param messagingTemplate the SimpMessagingTemplate
   */
  @Autowired
  public WebSocketMessageController(MessageService messageService,
      SimpMessagingTemplate messagingTemplate) {
    this.messageService = messageService;
    this.messagingTemplate = messagingTemplate;
  }

  /**
   * Sends a message via WebSocket.
   *
   * @param messageRequest the CreateMessageRequest
   * @return the created MessageDTO
   */
  @MessageMapping("/chat.sendMessage")
  @SendTo("/topic/messages")
  public MessageDTO sendMessage(@Payload CreateMessageRequest messageRequest) {
    logger.info("Received WebSocket message from: {}", messageRequest.getSenderId());
    return messageService.createMessage(messageRequest);
  }

  /**
   * Marks a message as read via WebSocket.
   *
   * @param readStatus the map containing messageId and read status
   */
  @MessageMapping("/chat.markRead")
  public void markMessageAsRead(@Payload Map<String, Object> readStatus) {
    Long messageId = null;

    // Extract message ID from payload
    if (readStatus.containsKey("messageId")) {
      messageId = Long.valueOf(readStatus.get("messageId").toString());
    }

    logger.info("Marking message as read: {}", messageId);

    if (messageId != null) {
      MessageDTO updatedMessage = messageService.markAsRead(messageId);

      // Notify clients that message has been marked as read
      messagingTemplate.convertAndSend("/topic/messages.read", updatedMessage);
    } else {
      logger.warn("No message ID provided in read status update");
    }
  }

  /**
   * Confirms delivery of a message.
   *
   * @param confirmationData the map containing messageId and delivery confirmation
   */
  @MessageMapping("/chat.confirmDelivery")
  public void confirmDelivery(@Payload Map<String, Object> confirmationData) {
    if (confirmationData.containsKey("messageId")) {
      String messageId = confirmationData.get("messageId").toString();
      logger.info("Message delivery confirmed: {}", messageId);

      // Notify clients of delivery confirmation
      messagingTemplate.convertAndSend("/topic/messages.delivered", confirmationData);
    }
  }

  /**
   * Broadcasts typing status updates.
   *
   * @param typingStatus the map containing userId, receiverId and typing status
   */
  @MessageMapping("/chat.typing")
  public void broadcastTypingStatus(@Payload Map<String, Object> typingStatus) {
    if (typingStatus.containsKey("userId") && typingStatus.containsKey("receiverId")) {
      String userId = typingStatus.get("userId").toString();
      String receiverId = typingStatus.get("receiverId").toString();
      boolean isTyping = Boolean.parseBoolean(typingStatus.get("isTyping").toString());

      logger.info("User {} typing status (to {}): {}", userId, receiverId, isTyping);

      // Forward typing status to all clients
      messagingTemplate.convertAndSend("/topic/messages.typing", typingStatus);
    }
  }
}
