package stud.ntnu.no.backend.message.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for handling chat messages.
 * <p>
 * This controller provides endpoints for WebSocket communication.
 */
@Controller
public class WebSocketController {

    @MessageMapping("/chat.ping")
    @SendTo("/topic/pong")
    /**
     * Handles a ping message and responds with a pong message.
     *
     * @return the string "pong"
     */
    public String handlePing() {
        return "pong";
    }
} 