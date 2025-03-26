package stud.ntnu.no.backend.Item.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.backend.Item.Entity.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySellerId(Long sellerId);
    List<Item> findByCategoryId(Long categoryId);
    List<Item> findByActiveTrue();
    List<Item> findByTitleContainingIgnoreCase(String title);
}