package stud.ntnu.no.backend.message.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * WebSocket controller for handling chat messages.
 *
 * <p>This controller provides endpoints for WebSocket communication.
 */
@Controller
public class WebSocketController {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

  @MessageMapping("/chat.ping")
  @SendTo("/topic/pong")
  /**
   * Handles a ping message and responds with a pong message.
   *
   * @return the string "pong"
   */
  public String handlePing() {
    logger.info("Received ping message");
    return "pong";
  }
}
