package stud.ntnu.no.backend.message.controller;

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
     * @param messageService the MessageService
     * @param messagingTemplate the SimpMessagingTemplate
     */
    @Autowired
    public WebSocketMessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
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
     * @param messageId the ID of the message to mark as read
     */
    @MessageMapping("/chat.markRead")
    public void markMessageAsRead(@Payload Long messageId) {
        logger.info("Marking message as read: {}", messageId);
        MessageDTO updatedMessage = messageService.markAsRead(messageId);

        // Notify clients that message has been marked as read
        messagingTemplate.convertAndSend("/topic/messages.read", updatedMessage);
    }
}
