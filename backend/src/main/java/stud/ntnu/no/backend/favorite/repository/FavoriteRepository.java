package stud.ntnu.no.backend.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stud.ntnu.no.backend.favorite.entity.Favorite;

import java.util.List;

/**
 * Repository interface for Favorite entity.
 * Provides methods to perform CRUD operations and custom queries.
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    /**
     * Finds all favorites by user ID.
     *
     * @param userId The ID of the user
     * @return A list of favorites for the specified user
     */
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId")
    List<Favorite> findByUserId(@Param("userId") String userId);
    /**
     * Finds all favorites by item ID.
     *
     * @param itemId The ID of the item
     * @return A list of favorites for the specified item
     */
    @Query("SELECT f FROM Favorite f WHERE f.item.id = :itemId")
    List<Favorite> findByItemId(@Param("itemId") Long itemId);
    
    /**
     * Checks if a favorite exists for a given user ID and item ID.
     *
     * @param userId The ID of the user
     * @param itemId The ID of the item
     * @return true if the favorite exists, otherwise false
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favorite f WHERE f.user.id = :userId AND f.item.id = :itemId")
    boolean existsByUserIdAndItemId(@Param("userId") String userId, @Param("itemId") Long itemId);
}