package stud.ntnu.no.backend.item.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.item.dto.ItemMarketPlaceDTO;
import stud.ntnu.no.backend.item.service.ItemMarketPlaceService;

/**
 * REST Controller for marketplace item operations.
 * <p>
 * This controller provides specialized endpoints for viewing items in the marketplace context.
 * Unlike the ItemController which offers complete item management functionality, this controller
 * focuses on public-facing marketplace views that do not require authentication.
 * </p>
 * <p>
 * The marketplace endpoints are optimized for browsing and discovery, returning data specifically
 * formatted for the marketplace user interface with relevant information for potential buyers.
 * </p>
 */
@RestController
@RequestMapping("/api/marketplace")
public class ItemMarketPlaceController {

  private static final Logger logger = LoggerFactory.getLogger(ItemMarketPlaceController.class);

  private final ItemMarketPlaceService itemMarketPlaceService;

  /**
   * Creates an instance of ItemMarketPlaceController.
   *
   * @param itemMarketPlaceService the service handling marketplace item operations
   */
  @Autowired
  public ItemMarketPlaceController(ItemMarketPlaceService itemMarketPlaceService) {
    this.itemMarketPlaceService = itemMarketPlaceService;
  }

  /**
   * Retrieves all active items available in the marketplace.
   * <p>
   * This endpoint returns a list of all active items formatted specifically for marketplace
   * display. The returned items include essential information for browsing and discovery, such as
   * title, price, location, and primary image.
   * </p>
   * <p>
   * This endpoint is intended for public access and does not require authentication. Only active
   * items are included in the results.
   * </p>
   *
   * @return ResponseEntity with HTTP status 200 (OK) and a list of ItemMarketPlaceDTO objects
   */
  @GetMapping("/items")
  public ResponseEntity<List<ItemMarketPlaceDTO>> getAllMarketPlaceItems() {
    logger.info("Fetching all marketplace items");
    List<ItemMarketPlaceDTO> items = itemMarketPlaceService.getAllMarketPlaceItems();
    return ResponseEntity.ok(items);
  }

  /**
   * Retrieves all active marketplace items within a specific category.
   * <p>
   * This endpoint returns items belonging to the specified category, formatted for marketplace
   * display. The results are filtered to include only active items that are available for
   * purchase.
   * </p>
   * <p>
   * This categorized view facilitates browsing by product type and helps users discover relevant
   * items. The endpoint is publicly accessible without authentication.
   * </p>
   *
   * @param categoryId the unique identifier of the category to filter by
   * @return ResponseEntity with HTTP status 200 (OK) and a list of ItemMarketPlaceDTO objects
   * filtered by the specified category
   */
  @GetMapping("/items/category/{categoryId}")
  public ResponseEntity<List<ItemMarketPlaceDTO>> getMarketPlaceItemsByCategory(
      @PathVariable Long categoryId) {
    logger.info("Fetching marketplace items for category with id: {}", categoryId);
    List<ItemMarketPlaceDTO> items = itemMarketPlaceService.getMarketPlaceItemsByCategory(
        categoryId);
    return ResponseEntity.ok(items);
  }
}
