package stud.ntnu.no.backend.item.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stud.ntnu.no.backend.common.security.model.CustomUserDetails;
import stud.ntnu.no.backend.item.service.ItemService;
import stud.ntnu.no.backend.item.dto.CreateItemDTO;
import stud.ntnu.no.backend.item.dto.ItemDTO;
import stud.ntnu.no.backend.user.entity.User;

import java.util.List;

/**
 * Controller for managing item operations.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    /**
     * Creates an instance of ItemController.
     * @param itemService the item service
     */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Returns active items.
     * @return ResponseEntity with list of active ItemDTO
     */
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        logger.info("Fetching all active items");
        return ResponseEntity.ok(itemService.getActiveItems());
    }

    /**
     * Returns all items including inactive ones.
     * @return ResponseEntity with list of ItemDTO
     */
    @GetMapping("/all")
    public ResponseEntity<List<ItemDTO>> getAllItemsIncludingInactive() {
        logger.info("Fetching all items including inactive ones");
        return ResponseEntity.ok(itemService.getAllItems());
    }

    /**
     * Returns an item by id.
     * @param id the item id
     * @return ResponseEntity with the corresponding ItemDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable Long id) {
        logger.info("Fetching item with id: {}", id);
        return ResponseEntity.ok(itemService.getItem(id));
    }

    /**
     * Returns items for the given seller id.
     * @param sellerId the seller id
     * @return ResponseEntity with list of ItemDTO
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ItemDTO>> getItemsBySeller(@PathVariable Long sellerId) {
        logger.info("Fetching items for seller with id: {}", sellerId);
        return ResponseEntity.ok(itemService.getItemsBySeller(sellerId));
    }

    /**
     * Returns items for the given category id.
     * @param categoryId the category id
     * @return ResponseEntity with list of ItemDTO
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ItemDTO>> getItemsByCategory(@PathVariable Long categoryId) {
        logger.info("Fetching items for category with id: {}", categoryId);
        return ResponseEntity.ok(itemService.getItemsByCategory(categoryId));
    }

    /**
     * Searches items based on the provided query.
     * @param query the search query
     * @return ResponseEntity with list of matching ItemDTO
     */
    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItems(@RequestParam String query) {
        logger.info("Searching items with query: {}", query);
        return ResponseEntity.ok(itemService.searchItems(query));
    }

    /**
     * Creates a new item.
     * @param itemDTO the item creation data
     * @return ResponseEntity with the created ItemDTO
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
     * Updates an existing item.
     * @param id the item id
     * @param itemDTO the item update data
     * @return ResponseEntity with the updated ItemDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody CreateItemDTO itemDTO) {
        logger.info("Updating item with id: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(itemService.updateItem(id, itemDTO, user.getId()));
    }

    /**
     * Deactivates an item.
     * @param id the item id
     * @return ResponseEntity with no content
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
     * Activates an item.
     * @param id the item id
     * @return ResponseEntity with no content
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
     * Deletes an item.
     * @param id the item id
     * @return ResponseEntity with no content
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
     * Returns the user id.
     * @return user id object
     */
    public Object getUserId() {
        return null;
    }
}
