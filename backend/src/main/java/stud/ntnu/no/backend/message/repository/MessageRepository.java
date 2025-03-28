package stud.ntnu.no.backend.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.message.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // You can add custom query methods here if needed
}