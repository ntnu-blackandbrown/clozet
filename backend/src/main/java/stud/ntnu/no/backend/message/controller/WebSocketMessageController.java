package stud.ntnu.no.backend.message.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.service.MessageService;

@Controller
public class WebSocketMessageController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageController.class);
    
    private final MessageService messageService;

    @Autowired
    public WebSocketMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/messages")
    public MessageDTO sendMessage(@Payload CreateMessageRequest messageRequest) {
        logger.info("Received WebSocket message from: {}", messageRequest.getSenderId());
        return messageService.createMessage(messageRequest);
    }
    
    @MessageMapping("/chat.markRead")
    public void markMessageAsRead(@Payload Long messageId) {
        logger.info("Marking message as read: {}", messageId);
        // You could implement this in your MessageService
        // messageService.markAsRead(messageId);
    }
}