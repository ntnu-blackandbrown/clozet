// CategoryRepository.java
package stud.ntnu.no.backend.Category.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.Category.Entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);
    boolean existsByName(String name);
}