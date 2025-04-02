package stud.ntnu.no.backend.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.favorite.entity.Favorite;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(String userId);
    List<Favorite> findByItemId(Long itemId);  // Changed from findByItemIdAndItemType
    boolean existsByUserIdAndItemId(String userId, Long itemId);
}