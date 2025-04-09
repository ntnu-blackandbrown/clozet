package stud.ntnu.no.backend.message.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.message.entity.Message;

/**
 * Repository interface for Message entities.
 *
 * <p>This interface extends JpaRepository to provide CRUD operations for Message entities.
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
}
