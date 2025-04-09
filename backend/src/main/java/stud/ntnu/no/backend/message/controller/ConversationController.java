package stud.ntnu.no.backend.message.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.message.dto.ConversationDTO;
import stud.ntnu.no.backend.message.service.ConversationService;

/**
 * REST controller for managing conversations.
 *
 * <p>This controller provides endpoints for retrieving and archiving user conversations.
 */
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

  private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);

  private final ConversationService conversationService;

  /**
   * Constructs a new ConversationController with the specified service.
   *
   * @param conversationService the ConversationService
   */
  @Autowired
  public ConversationController(ConversationService conversationService) {
    this.conversationService = conversationService;
  }

  /**
   * Retrieves all conversations for a user.
   *
   * @param userId the ID of the user
   * @return a list of ConversationDTOs
   */
  @GetMapping
  public ResponseEntity<List<ConversationDTO>> getUserConversations(@RequestParam String userId) {
    logger.info("Retrieving conversations for user: {}", userId);
    return ResponseEntity.ok(conversationService.getUserConversations(userId));
  }

  /**
   * Archives a conversation for a user.
   *
   * @param conversationId the ID of the conversation
   * @param userId the ID of the user
   * @return a ResponseEntity with status 200 (OK)
   */
  @PostMapping("/{conversationId}/archive")
  public ResponseEntity<Void> archiveConversation(
      @PathVariable String conversationId, @RequestParam String userId) {
    logger.info("Archiving conversation: {} for user: {}", conversationId, userId);
    conversationService.archiveConversation(conversationId, userId);
    return ResponseEntity.ok().build();
  }
}
