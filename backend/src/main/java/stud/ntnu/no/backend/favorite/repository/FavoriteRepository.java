package stud.ntnu.no.backend.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stud.ntnu.no.backend.favorite.entity.Favorite;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId")
    List<Favorite> findByUserId(@Param("userId") Long userId);
    @Query("SELECT f FROM Favorite f WHERE f.item.id = :itemId")
    List<Favorite> findByItemId(@Param("itemId") Long itemId);
    
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favorite f WHERE f.user.id = :userId AND f.item.id = :itemId")
    boolean existsByUserIdAndItemId(@Param("userId") String userId, @Param("itemId") Long itemId);
}