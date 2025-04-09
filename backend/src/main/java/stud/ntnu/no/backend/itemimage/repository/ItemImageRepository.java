package stud.ntnu.no.backend.itemimage.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;

/**
 * Repository interface for ItemImage entity. Provides methods to perform CRUD operations and custom
 * queries.
 */
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {

  /**
   * Finds all item images by item ID.
   *
   * @param itemId The ID of the item
   * @return A list of item images for the specified item
   */
  List<ItemImage> findByItemId(Long itemId);

  /**
   * Finds all item images by item ID, ordered by display order.
   *
   * @param itemId The ID of the item
   * @return A list of item images for the specified item, ordered by display order
   */
  List<ItemImage> findByItemIdOrderByDisplayOrder(Long itemId);
}
