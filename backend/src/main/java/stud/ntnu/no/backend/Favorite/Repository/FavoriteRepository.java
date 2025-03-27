package stud.ntnu.no.backend.Favorite.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.Favorite.Entity.Favorite;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(String userId);
    List<Favorite> findByItemIdAndItemType(Long itemId, String itemType);
}