package stud.ntnu.no.backend.Message.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.Message.Entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // You can add custom query methods here if needed
}