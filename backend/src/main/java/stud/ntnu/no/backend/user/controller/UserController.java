package stud.ntnu.no.backend.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.user.dto.UpdateUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.service.UserService;
import stud.ntnu.no.backend.user.entity.User;
// Entity imports for JPQL queries
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.history.entity.History;
import stud.ntnu.no.backend.review.entity.Review;
import stud.ntnu.no.backend.user.entity.VerificationToken;
import stud.ntnu.no.backend.user.entity.PasswordResetToken;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.service.WebSocketService;
import stud.ntnu.no.backend.message.service.ConversationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST controller for managing user-related operations.
 * <p>
 * This controller provides API endpoints for managing user accounts,
 * including retrieving user information, updating user profiles, and
 * deleting user accounts. It handles administrative operations on users
 * and is separate from the authentication and registration processes.
 * </p>
 * <p>
 * All endpoints return appropriate HTTP status codes and structured response data.
 * Many operations may require administrative privileges or ownership of the resource.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    
    @Autowired
    private WebSocketService webSocketService;
    
    @Autowired
    private ConversationService conversationService;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves all users in the system.
     * <p>
     * This endpoint returns a collection of all user profiles with sensitive
     * information removed. It is typically restricted to administrators.
     * </p>
     *
     * @return a list of UserDTOs with HTTP status 200 (OK)
     */
    @GetMapping
    public List<UserDTO> getAllUsers() {
        logger.debug("Retrieving all users");
        return userService.getAllUsers();
    }

    /**
     * Retrieves a specific user by their unique identifier.
     * <p>
     * This endpoint attempts to find and return a user that matches the provided ID.
     * </p>
     *
     * @param id the unique identifier of the user to retrieve
     * @return the UserDTO with HTTP status 200 (OK)
     * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID exists
     */
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        logger.debug("Retrieving user with ID: {}", id);
        return userService.getUserById(id);
    }

    /**
     * Updates an existing user's information.
     * <p>
     * This endpoint updates a user record with the provided data.
     * Only specific fields can be updated, and some fields (like email or username)
     * may require validation to ensure uniqueness.
     * </p>
     *
     * @param id the unique identifier of the user to update
     * @param updateUserDTO the data transfer object containing updated user information
     * @return the updated UserDTO with HTTP status 200 (OK)
     * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID exists
     * @throws stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException if the new username is already taken
     * @throws stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException if the new email is already in use
     */
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        logger.debug("Updating user with ID: {}", id);
        return userService.updateUser(id, updateUserDTO);
    }

    /**
     * Deletes a user from the system.
     * <p>
     * This endpoint removes a user account and potentially all associated data.
     * This operation is typically irreversible and should be used with caution.
     * </p>
     *
     * @param id the unique identifier of the user to delete
     * @return a ResponseEntity with HTTP status 204 (No Content) indicating successful deletion
     * @throws stud.ntnu.no.backend.user.exception.UserNotFoundException if no user with the given ID exists
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        // Mark user as deleted in conversations
        conversationService.markUserAsDeleted(id.toString());
        
        // Delete related records
        deleteUserRelatedRecords(id);
        
        // Delete the user itself
        userService.deleteUser(id);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Deletes all related records of a user before deleting the user itself.
     * This is necessary to avoid constraint violations in the database.
     * 
     * @param userId the ID of the user whose related records are to be deleted
     */
    private void deleteUserRelatedRecords(Long userId) {
        logger.debug("Deleting related records for user with ID: {}", userId);
        
        // Delete verification tokens
        Query deleteVerificationTokens = entityManager.createQuery(
            "DELETE FROM VerificationToken vt WHERE vt.user.id = :userId");
        deleteVerificationTokens.setParameter("userId", userId);
        deleteVerificationTokens.executeUpdate();
        
        // Delete password reset tokens
        Query deletePasswordResetTokens = entityManager.createQuery(
            "DELETE FROM PasswordResetToken prt WHERE prt.user.id = :userId");
        deletePasswordResetTokens.setParameter("userId", userId);
        deletePasswordResetTokens.executeUpdate();
        
        // Delete favorites - this includes favorites created by this user and favorites of items owned by this user
        Query deleteFavorites = entityManager.createQuery(
            "DELETE FROM Favorite f WHERE f.user.id = :userId OR f.item.seller.id = :userId");
        deleteFavorites.setParameter("userId", userId);
        deleteFavorites.executeUpdate();
        
        // Delete browsing history
        Query deleteHistory = entityManager.createQuery(
            "DELETE FROM History h WHERE h.user.id = :userId");
        deleteHistory.setParameter("userId", userId);
        deleteHistory.executeUpdate();
        
        // Find all unique conversation IDs involving this user
        Query findConversationIDsQuery = entityManager.createQuery(
            "SELECT DISTINCT CASE " +
            "WHEN CAST(m.senderId AS long) < CAST(m.receiverId AS long) THEN CONCAT(m.senderId, '_', m.receiverId, '_', COALESCE(m.item.id, 'null')) " +
            "ELSE CONCAT(m.receiverId, '_', m.senderId, '_', COALESCE(m.item.id, 'null')) " +
            "END AS conversationId " +
            "FROM Message m WHERE m.senderId = :userIdString OR m.receiverId = :userIdString");
        findConversationIDsQuery.setParameter("userIdString", userId.toString());
        List<String> conversationIds = findConversationIDsQuery.getResultList();
        
        // Instead of deleting messages, archive them for both sides of the conversation
        Query archiveMessagesAsSender = entityManager.createQuery(
            "UPDATE Message m SET m.archivedBySender = true WHERE m.senderId = :userIdString");
        archiveMessagesAsSender.setParameter("userIdString", userId.toString());
        int archivedSenderMessages = archiveMessagesAsSender.executeUpdate();
        
        Query archiveMessagesAsReceiver = entityManager.createQuery(
            "UPDATE Message m SET m.archivedByReceiver = true WHERE m.receiverId = :userIdString");
        archiveMessagesAsReceiver.setParameter("userIdString", userId.toString());
        int archivedReceiverMessages = archiveMessagesAsReceiver.executeUpdate();
        
        logger.debug("Archived {} messages as sender and {} messages as receiver for user with ID: {}", 
            archivedSenderMessages, archivedReceiverMessages, userId);
        
        // Notify via WebSocket that these conversations have been archived due to user deletion
        for (String conversationId : conversationIds) {
            webSocketService.notifyConversationArchived(conversationId, userId.toString());
        }
        
        // Delete transactions related to items owned by the user
        Query deleteTransactions = entityManager.createQuery(
            "DELETE FROM Transaction t WHERE t.item.seller.id = :userId OR t.buyer.id = :userId");
        deleteTransactions.setParameter("userId", userId);
        deleteTransactions.executeUpdate();
        
        // Delete reviews where the user is either the reviewer or reviewee
        Query deleteReviewsAsReviewer = entityManager.createQuery(
            "DELETE FROM Review r WHERE r.reviewer.id = :userId");
        deleteReviewsAsReviewer.setParameter("userId", userId);
        deleteReviewsAsReviewer.executeUpdate();
        
        Query deleteReviewsAsReviewee = entityManager.createQuery(
            "DELETE FROM Review r WHERE r.reviewee.id = :userId");
        deleteReviewsAsReviewee.setParameter("userId", userId);
        deleteReviewsAsReviewee.executeUpdate();
        
        // Delete all items owned by the user
        // The cascade types on Item entity will handle deleting:
        // - ItemImages (cascade = CascadeType.ALL, orphanRemoval = true)
        // - History records for the item (cascade = CascadeType.ALL, orphanRemoval = true)
        Query deleteItems = entityManager.createQuery(
            "DELETE FROM Item i WHERE i.seller.id = :userId");
        deleteItems.setParameter("userId", userId);
        int deletedItems = deleteItems.executeUpdate();
        logger.debug("Deleted {} items owned by user with ID: {}", deletedItems, userId);
        
        logger.debug("Successfully deleted all related records for user with ID: {}", userId);
    }
}
