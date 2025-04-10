package stud.ntnu.no.backend.item.service;


import java.util.List;
import stud.ntnu.no.backend.item.dto.CreateItemDTO;
import stud.ntnu.no.backend.item.dto.ItemDTO;

/**
 * Service interface for managing item operations.
 * <p>
 * This interface defines the core business operations for managing items in the marketplace. It
 * provides methods for creating, retrieving, updating, and deleting items, as well as specialized
 * operations like activating and deactivating items. All methods that modify items include seller
 * validation to ensure that only the item owner can make changes.
 * </p>
 */
public interface ItemService {

  /**
   * Retrieves all items in the system.
   *
   * @return a list of all items, including both active and inactive ones
   */
  List<ItemDTO> getAllItems();

  /**
   * Retrieves only active (available) items.
   *
   * @return a list of items that are currently marked as available
   */
  List<ItemDTO> getActiveItems();

  /**
   * Retrieves a specific item by its ID.
   *
   * @param id the unique identifier of the item to retrieve
   * @return the item with the specified ID
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if no item exists with the
   *                                                                   given ID
   */
  ItemDTO getItem(Long id);

  /**
   * Retrieves all items listed by a specific seller.
   *
   * @param sellerId the unique identifier of the seller
   * @return a list of items owned by the specified seller
   */
  List<ItemDTO> getItemsBySeller(Long sellerId);

  /**
   * Retrieves all items in a specific category.
   *
   * @param categoryId the unique identifier of the category
   * @return a list of items belonging to the specified category
   */
  List<ItemDTO> getItemsByCategory(Long categoryId);

  /**
   * Searches for items matching the provided query string.
   *
   * @param query the search query to match against item data
   * @return a list of items that match the search criteria
   */
  List<ItemDTO> searchItems(String query);

  /**
   * Creates a new item listing.
   *
   * @param itemDTO  the data transfer object containing the item details
   * @param sellerId the unique identifier of the seller creating the item
   * @return the created item with its generated ID
   * @throws stud.ntnu.no.backend.item.exception.ItemValidationException if the item data is
   *                                                                     invalid
   */
  ItemDTO createItem(CreateItemDTO itemDTO, Long sellerId);

  /**
   * Updates an existing item with new information.
   *
   * @param id       the unique identifier of the item to update
   * @param itemDTO  the data transfer object containing the updated item details
   * @param sellerId the unique identifier of the seller making the update (for ownership
   *                 verification)
   * @return the updated item
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException   if no item exists with the
   *                                                                     given ID
   * @throws stud.ntnu.no.backend.item.exception.ItemValidationException if the item data is
   *                                                                     invalid
   * @throws org.springframework.security.access.AccessDeniedException   if the specified seller is
   *                                                                     not the owner of the item
   */
  ItemDTO updateItem(Long id, CreateItemDTO itemDTO, Long sellerId);

  /**
   * Deactivates an item, making it temporarily unavailable in the marketplace.
   *
   * @param id       the unique identifier of the item to deactivate
   * @param sellerId the unique identifier of the seller making the change (for ownership
   *                 verification)
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if no item exists with the
   *                                                                   given ID
   * @throws org.springframework.security.access.AccessDeniedException if the specified seller is
   *                                                                   not the owner of the item
   */
  void deactivateItem(Long id, Long sellerId);

  /**
   * Activates a previously deactivated item, making it visible in the marketplace again.
   *
   * @param id       the unique identifier of the item to activate
   * @param sellerId the unique identifier of the seller making the change (for ownership
   *                 verification)
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if no item exists with the
   *                                                                   given ID
   * @throws org.springframework.security.access.AccessDeniedException if the specified seller is
   *                                                                   not the owner of the item
   */
  void activateItem(Long id, Long sellerId);

  /**
   * Permanently deletes an item from the system.
   *
   * @param id       the unique identifier of the item to delete
   * @param sellerId the unique identifier of the seller making the change (for ownership
   *                 verification)
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if no item exists with the
   *                                                                   given ID
   * @throws org.springframework.security.access.AccessDeniedException if the specified seller is
   *                                                                   not the owner of the item
   */
  void deleteItem(Long id, Long sellerId);
}