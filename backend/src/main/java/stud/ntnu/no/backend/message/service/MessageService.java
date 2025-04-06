package stud.ntnu.no.backend.message.service;

import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.dto.UpdateMessageRequest;

import java.util.List;

/**
 * Service interface for managing messages.
 * <p>
 * This interface defines methods for CRUD operations on messages.
 */
public interface MessageService {
    /**
     * Retrieves all messages.
     *
     * @return a list of MessageDTOs
     */
    List<MessageDTO> getAllMessages();

    /**
     * Retrieves a message by its ID.
     *
     * @param id the ID of the message
     * @return the MessageDTO
     */
    MessageDTO getMessageById(Long id);

    /**
     * Creates a new message.
     *
     * @param request the CreateMessageRequest
     * @return the created MessageDTO
     */
    MessageDTO createMessage(CreateMessageRequest request);

    /**
     * Updates an existing message.
     *
     * @param id the ID of the message to update
     * @param request the UpdateMessageRequest with updated information
     * @return the updated MessageDTO
     */
    MessageDTO updateMessage(Long id, UpdateMessageRequest request);

    /**
     * Deletes a message by its ID.
     *
     * @param id the ID of the message to delete
     */
    void deleteMessage(Long id);

    /**
     * Marks a message as read.
     *
     * @param messageId the ID of the message to mark as read
     * @return the updated MessageDTO
     */
    MessageDTO markAsRead(Long messageId);
}