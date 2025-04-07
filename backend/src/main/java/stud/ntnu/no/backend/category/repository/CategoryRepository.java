package stud.ntnu.no.backend.category.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.category.entity.Category;

import java.util.List;

/**
 * Repository interface for database operations related to Category entities.
 * Extends JpaRepository to inherit standard CRUD operations.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds all subcategories for a given parent category.
     *
     * @param parentId ID of the parent category
     * @return List of subcategories
     */
    List<Category> findByParentId(Long parentId);

    /**
     * Checks if a category with the given name already exists.
     *
     * @param name Name to check for existence
     * @return true if a category with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds categories ordered by the number of times items in the category have been favorited.
     * Uses pagination to limit the result set.
     *
     * @param pageable Pagination information
     * @return List of categories ordered by popularity
     */
    @Query("SELECT c FROM Category c JOIN c.items i JOIN i.favorites f GROUP BY c ORDER BY COUNT(f) DESC")
    List<Category> findTopCategoriesByFavoriteCount(Pageable pageable);

    /**
     * Finds categories ordered by the number of items in the category.
     * Uses pagination to limit the result set.
     *
     * @param pageable Pagination information
     * @return List of categories ordered by item count
     */
    @Query("SELECT c FROM Category c LEFT JOIN c.items i GROUP BY c ORDER BY COUNT(i) DESC")
    List<Category> findTopCategoriesByItemCount(Pageable pageable);
}
