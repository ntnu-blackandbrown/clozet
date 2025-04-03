package stud.ntnu.no.backend.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.history.entity.History;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserOrderByViewedAtDesc(User user);
    List<History> findByUserAndActiveOrderByViewedAtDesc(User user, boolean active);
    Optional<History> findByUserAndItem(User user, Item item);
    void deleteByUser(User user);
}