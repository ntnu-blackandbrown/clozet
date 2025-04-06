package stud.ntnu.no.backend.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Transaction entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations for Transaction entities.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
     * Finds transactions created between the specified start and end dates.
     *
     * @param start the start date and time
     * @param end the end date and time
     * @return a list of transactions
     */
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}