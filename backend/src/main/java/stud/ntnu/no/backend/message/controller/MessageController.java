package stud.ntnu.no.backend.message.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.dto.UpdateMessageRequest;
import stud.ntnu.no.backend.message.service.MessageService;

import java.util.List;

/**
 * REST controller for managing messages.
 * <p>
 * This controller provides endpoints for CRUD operations on messages.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    /**
     * Constructs a new MessageController with the specified service.
     *
     * @param messageService the MessageService
     */
    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Retrieves all messages.
     *
     * @return a list of MessageDTOs
     */
    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        logger.info("Retrieving all messages");
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    /**
     * Retrieves a message by its ID.
     *
     * @param id the ID of the message
     * @return the MessageDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        logger.info("Retrieving message with ID: {}", id);
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    /**
     * Creates a new message.
     *
     * @param request the CreateMessageRequest
     * @return the created MessageDTO
     */
    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@RequestBody CreateMessageRequest request) {
        logger.info("Creating new message from sender: {}", request.getSenderId());
        return new ResponseEntity<>(messageService.createMessage(request), HttpStatus.CREATED);
    }

    /**
     * Updates an existing message.
     *
     * @param id the ID of the message to update
     * @param request the UpdateMessageRequest with updated information
     * @return the updated MessageDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> updateMessage(
            @PathVariable Long id,
            @RequestBody UpdateMessageRequest request) {
        logger.info("Updating message with ID: {}", id);
        return ResponseEntity.ok(messageService.updateMessage(id, request));
    }

    /**
     * Deletes a message by its ID.
     *
     * @param id the ID of the message to delete
     * @return a ResponseEntity with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        logger.info("Deleting message with ID: {}", id);
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
