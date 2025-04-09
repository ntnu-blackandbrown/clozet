package stud.ntnu.no.backend.item.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.backend.item.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

  List<Item> findBySellerId(Long sellerId);

  List<Item> findByCategoryId(Long categoryId);

  List<Item> findByIsAvailableTrue(); // Changed from findByAvailableTrue()

  List<Item> findByTitleContainingIgnoreCase(String title);

  List<Item> findByCategoryIdAndIsAvailableTrue(Long categoryId);
  // Other methods...
}
