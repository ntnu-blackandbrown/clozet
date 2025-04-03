package stud.ntnu.no.backend.message.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import stud.ntnu.no.backend.message.dto.MessageDTO;

@Service
public class WebSocketService {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

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

    public void notifyMessageUpdated(MessageDTO message) {
        try {
            logger.info("Broadcasting message update with ID: {}", message.getId());
            messagingTemplate.convertAndSend("/topic/messages.update", message);
        } catch (Exception e) {
            logger.error("Error broadcasting message update: {}", e.getMessage(), e);
        }
    }

    public void notifyMessageDeleted(Long messageId) {
        try {
            logger.info("Broadcasting message deletion with ID: {}", messageId);
            messagingTemplate.convertAndSend("/topic/messages.delete", messageId);
        } catch (Exception e) {
            logger.error("Error broadcasting message deletion: {}", e.getMessage(), e);
        }
    }
}