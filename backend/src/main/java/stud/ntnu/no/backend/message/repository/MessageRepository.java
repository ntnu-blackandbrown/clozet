package stud.ntnu.no.backend.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stud.ntnu.no.backend.message.entity.Message;

import java.util.List;

/**
 * Repository interface for Message entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations for Message entities.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {
    // You can add custom query methods here if needed
    /**
     * Finds messages by sender or receiver ID.
     *
     * @param senderId the sender ID
     * @param receiverId the receiver ID
     * @return a list of messages
     */
    List<Message> findBySenderIdOrReceiverId(String senderId, String receiverId);
    
    /**
     * Finds messages by conversation ID.
     * A conversation ID is a composite ID made up of the sorted sender and receiver IDs,
     * plus the item ID (e.g., "1_2_3" for users 1 and 2, item 3).
     *
     * @param conversationId the conversation ID
     * @return a list of messages
     */
    @Query("SELECT m FROM Message m WHERE " +
           "CASE " +
           "WHEN CAST(m.senderId AS long) < CAST(m.receiverId AS long) THEN CONCAT(m.senderId, '_', m.receiverId, '_', COALESCE(m.item.id, 'null')) " +
           "ELSE CONCAT(m.receiverId, '_', m.senderId, '_', COALESCE(m.item.id, 'null')) " +
           "END = :conversationId")
    List<Message> findByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * Finds non-archived messages by user ID.
     *
     * @param userId the user ID
     * @return a list of messages
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId AND m.archivedBySender = false) OR " +
           "(m.receiverId = :userId AND m.archivedByReceiver = false)")
    List<Message> findNonArchivedMessagesByUserId(@Param("userId") String userId);
}