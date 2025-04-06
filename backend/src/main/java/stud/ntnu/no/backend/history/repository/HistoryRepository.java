package stud.ntnu.no.backend.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.history.entity.History;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    /**
     * Finds the history entries for a user, ordered by the viewed date in descending order.
     *
     * @param user the user
     * @return the list of history entries
     */
    List<History> findByUserOrderByViewedAtDesc(User user);

    /**
     * Finds the active history entries for a user, ordered by the viewed date in descending order.
     *
     * @param user the user
     * @param active the active status
     * @return the list of active history entries
     */
    List<History> findByUserAndActiveOrderByViewedAtDesc(User user, boolean active);

    /**
     * Finds a history entry by user and item.
     *
     * @param user the user
     * @param item the item
     * @return the optional history entry
     */
    Optional<History> findByUserAndItem(User user, Item item);

    /**
     * Deletes all history entries for a user.
     *
     * @param user the user
     */
    void deleteByUser(User user);
}
