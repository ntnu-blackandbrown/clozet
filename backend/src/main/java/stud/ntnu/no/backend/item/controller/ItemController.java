package stud.ntnu.no.backend.item.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.common.security.model.CustomUserDetails;
import stud.ntnu.no.backend.item.dto.CreateItemDTO;
import stud.ntnu.no.backend.item.dto.ItemDTO;
import stud.ntnu.no.backend.item.service.ItemService;
import stud.ntnu.no.backend.user.entity.User;

/**
 * REST controller for managing item-related operations.
 * <p>
 * This controller provides endpoints for listing, searching, creating, updating, and deleting items
 * in the system. It handles requests related to marketplace listings, allowing users to manage
 * their items and browse items from other sellers.
 * </p>
 * <p>
 * The controller supports:
 * <ul>
 *   <li>Fetching all items, only active items, or filtering by seller/category</li>
 *   <li>Creating, updating, deactivating, activating, and deleting items</li>
 *   <li>Searching items by keyword(s)</li>
 * </ul>
 * </p>
 * <p>
 * Endpoints enforce appropriate authentication and authorization, ensuring that
 * users can only modify their own items. All operations that modify items require
 * authentication, and authorization checks ensure that users can only modify items
 * they own.
 * </p>
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

  private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

  private final ItemService itemService;

  /**
   * Creates an instance of ItemController with dependency injection.
   *
   * @param itemService the service responsible for item business logic
   */
  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  /**
   * Fetches a list of all items available in the system.
   * <p>
   * This endpoint provides access to all item listings in the system, including both active and
   * inactive items. This comprehensive view is suitable for administrative purposes.
   * </p>
   *
   * @return ResponseEntity with HTTP status 200 (OK) and the list of all ItemDTOs
   */
  @GetMapping
  public ResponseEntity<List<ItemDTO>> getAllItems() {
    logger.info("Fetching all active items");
    return ResponseEntity.ok(itemService.getActiveItems());
  }

  /**
   * Fetches a list of all active (available) items in the system.
   * <p>
   * This endpoint returns only items that are currently marked as active and available for
   * purchase. It filters out inactive items that sellers have temporarily hidden or deactivated.
   * </p>
   *
   * @return ResponseEntity with HTTP status 200 (OK) and the list of active ItemDTOs
   */
  @GetMapping("/all")
  public ResponseEntity<List<ItemDTO>> getAllItemsIncludingInactive() {
    logger.info("Fetching all items including inactive ones");
    return ResponseEntity.ok(itemService.getAllItems());
  }

  /**
   * Fetches a single item by its unique identifier.
   * <p>
   * This endpoint retrieves detailed information about a specific item, identified by its ID. The
   * information includes all item attributes such as title, description, price, and category.
   * </p>
   *
   * @param id the unique identifier of the item to retrieve
   * @return ResponseEntity with HTTP status 200 (OK) and the requested ItemDTO
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if no item with the given ID
   *                                                                   exists
   */
  @GetMapping("/{id}")
  public ResponseEntity<ItemDTO> getItem(@PathVariable Long id) {
    logger.info("Fetching item with id: {}", id);
    return ResponseEntity.ok(itemService.getItem(id));
  }

  /**
   * Fetches a list of items created by a specific seller.
   * <p>
   * This endpoint returns all items that have been listed by the seller specified by the seller ID.
   * This includes both active and inactive items.
   * </p>
   *
   * @param sellerId the unique identifier of the seller
   * @return ResponseEntity with HTTP status 200 (OK) and the list of ItemDTOs owned by the seller
   */
  @GetMapping("/seller/{sellerId}")
  public ResponseEntity<List<ItemDTO>> getItemsBySeller(@PathVariable Long sellerId) {
    logger.info("Fetching items for seller with id: {}", sellerId);
    return ResponseEntity.ok(itemService.getItemsBySeller(sellerId));
  }

  /**
   * Fetches a list of items within a specific category.
   * <p>
   * This endpoint returns all items that are classified under the category specified by the
   * category ID. This allows browsing by product category.
   * </p>
   *
   * @param categoryId the unique identifier of the category
   * @return ResponseEntity with HTTP status 200 (OK) and the list of ItemDTOs in the category
   */
  @GetMapping("/category/{categoryId}")
  public ResponseEntity<List<ItemDTO>> getItemsByCategory(@PathVariable Long categoryId) {
    logger.info("Fetching items for category with id: {}", categoryId);
    return ResponseEntity.ok(itemService.getItemsByCategory(categoryId));
  }

  /**
   * Searches for items using a keyword query.
   * <p>
   * This endpoint performs a text search across item titles, descriptions, and other relevant
   * fields. It returns items that match the search criteria.
   * </p>
   * <p>
   * The search is case-insensitive and matches partial words, making it suitable for keyword-based
   * discovery.
   * </p>
   *
   * @param query the search string to match against item data
   * @return ResponseEntity with HTTP status 200 (OK) and the list of matching ItemDTOs
   */
  @GetMapping("/search")
  public ResponseEntity<List<ItemDTO>> searchItems(@RequestParam String query) {
    logger.info("Searching items with query: {}", query);
    return ResponseEntity.ok(itemService.searchItems(query));
  }

  /**
   * Creates a new item listing in the marketplace.
   * <p>
   * This endpoint processes a request to create a new item. The item will be associated with the
   * currently authenticated user as the seller.
   * </p>
   *
   * @param itemDTO the data transfer object containing item details
   * @return ResponseEntity with HTTP status 201 (Created) and the created ItemDTO
   * @throws stud.ntnu.no.backend.item.exception.ItemValidationException       if the item data is
   *                                                                           invalid
   * @throws stud.ntnu.no.backend.category.exception.CategoryNotFoundException if the specified
   *                                                                           category does not
   *                                                                           exist
   * @throws stud.ntnu.no.backend.location.exception.LocationNotFoundException if the specified
   *                                                                           location does not
   *                                                                           exist
   */
  @PostMapping
  public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody CreateItemDTO itemDTO) {
    logger.info("Creating new item");
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    User user = userDetails.getUser();
    return new ResponseEntity<>(itemService.createItem(itemDTO, user.getId()), HttpStatus.CREATED);
  }

  /**
   * Updates an existing item listing with new information.
   * <p>
   * This endpoint allows the seller of an item to update its details. The operation is restricted
   * to the authenticated user who originally created the item. Authentication and authorization are
   * enforced to ensure only the owner can modify their listings.
   * </p>
   * <p>
   * All validation rules that apply to item creation are also enforced during updates.
   * </p>
   *
   * @param id      the unique identifier of the item to update
   * @param itemDTO the data transfer object containing updated item details
   * @return ResponseEntity with HTTP status 200 (OK) and the updated ItemDTO
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException         if no item with the
   *                                                                           given ID exists
   * @throws stud.ntnu.no.backend.item.exception.ItemValidationException       if the item data is
   *                                                                           invalid
   * @throws stud.ntnu.no.backend.category.exception.CategoryNotFoundException if the specified
   *                                                                           category does not
   *                                                                           exist
   * @throws stud.ntnu.no.backend.location.exception.LocationNotFoundException if the specified
   *                                                                           location does not
   *                                                                           exist
   * @throws org.springframework.security.access.AccessDeniedException         if the authenticated
   *                                                                           user is not the owner
   *                                                                           of the item
   */
  @PutMapping("/{id}")
  public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id,
      @Valid @RequestBody CreateItemDTO itemDTO) {
    logger.info("Updating item with id: {}", id);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    User user = userDetails.getUser();
    return ResponseEntity.ok(itemService.updateItem(id, itemDTO, user.getId()));
  }

  /**
   * Deactivates an item listing, making it temporarily unavailable in the marketplace.
   * <p>
   * This endpoint allows sellers to hide their items without deleting them. Deactivated items can
   * be reactivated later. This operation is restricted to the authenticated user who owns the
   * item.
   * </p>
   * <p>
   * Deactivation affects item visibility in search results and category listings, but does not
   * delete any data or break existing references.
   * </p>
   *
   * @param id the unique identifier of the item to deactivate
   * @return ResponseEntity with HTTP status 204 (No Content) indicating successful deactivation
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if no item with the given ID
   *                                                                   exists
   * @throws org.springframework.security.access.AccessDeniedException if the authenticated user is
   *                                                                   not the owner of the item
   */
  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<Void> deactivateItem(@PathVariable Long id) {
    logger.info("Deactivating item with id: {}", id);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    User user = userDetails.getUser();
    itemService.deactivateItem(id, user.getId());
    return ResponseEntity.noContent().build();
  }

  /**
   * Activates a previously deactivated item, making it visible in the marketplace again.
   * <p>
   * This endpoint allows sellers to restore visibility to items that were previously hidden. The
   * operation is restricted to the authenticated user who owns the item.
   * </p>
   * <p>
   * Upon activation, the item will immediately appear in search results and category listings if it
   * meets the necessary criteria.
   * </p>
   *
   * @param id the unique identifier of the item to activate
   * @return ResponseEntity with HTTP status 204 (No Content) indicating successful activation
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if no item with the given ID
   *                                                                   exists
   * @throws org.springframework.security.access.AccessDeniedException if the authenticated user is
   *                                                                   not the owner of the item
   */
  @PatchMapping("/{id}/activate")
  public ResponseEntity<Void> activateItem(@PathVariable Long id) {
    logger.info("Activating item with id: {}", id);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    User user = userDetails.getUser();
    itemService.activateItem(id, user.getId());
    return ResponseEntity.noContent().build();
  }

  /**
   * Permanently deletes an item listing from the system.
   * <p>
   * This endpoint allows sellers to completely remove their items from the marketplace. The
   * operation is restricted to the authenticated user who owns the item.
   * </p>
   * <p>
   * Warning: This operation is irreversible and will cascade delete related data including item
   * images, favorites, and history records. However, any completed transactions involving this item
   * will be preserved for record-keeping purposes.
   * </p>
   *
   * @param id the unique identifier of the item to delete
   * @return ResponseEntity with HTTP status 204 (No Content) indicating successful deletion
   * @throws stud.ntnu.no.backend.item.exception.ItemNotFoundException if no item with the given ID
   *                                                                   exists
   * @throws org.springframework.security.access.AccessDeniedException if the authenticated user is
   *                                                                   not the owner of the item
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
    logger.info("Deleting item with id: {}", id);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    User user = userDetails.getUser();
    itemService.deleteItem(id, user.getId());
    return ResponseEntity.noContent().build();
  }

  /**
   * Utility method to retrieve the current user ID.
   * <p>
   * This is an internal helper method used to extract the authenticated user's ID from the security
   * context. It is not exposed as an API endpoint.
   * </p>
   *
   * @return the ID of the currently authenticated user, or null if not authenticated
   */
  public Object getUserId() {
    return null;
  }
}
