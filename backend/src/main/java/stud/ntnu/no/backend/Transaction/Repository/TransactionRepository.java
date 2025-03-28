package stud.ntnu.no.backend.Transaction.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.Transaction.Entity.Transaction;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}