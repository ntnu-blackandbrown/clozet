package stud.ntnu.no.backend.category.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.category.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);
    boolean existsByName(String name);
    
    // Updated query with pagination support
    @Query("SELECT c FROM Category c JOIN c.items i JOIN i.favorites f GROUP BY c ORDER BY COUNT(f) DESC")
    List<Category> findTopCategoriesByFavoriteCount(Pageable pageable);
}