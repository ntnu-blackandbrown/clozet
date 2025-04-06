package stud.ntnu.no.backend.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.item.dto.ItemMarketPlaceDTO;
import stud.ntnu.no.backend.item.service.ItemMarketPlaceService;

import java.util.List;

/**
 * Controller for marketplace item operations.
 */
@RestController
@RequestMapping("/api/marketplace")
public class ItemMarketPlaceController {

    private final ItemMarketPlaceService itemMarketPlaceService;

    /**
     * Creates an instance of ItemMarketPlaceController.
     * @param itemMarketPlaceService the marketplace item service
     */
    @Autowired
    public ItemMarketPlaceController(ItemMarketPlaceService itemMarketPlaceService) {
        this.itemMarketPlaceService = itemMarketPlaceService;
    }

    /**
     * Returns all marketplace items.
     * @return ResponseEntity with list of ItemMarketPlaceDTO
     */
    @GetMapping("/items")
    public ResponseEntity<List<ItemMarketPlaceDTO>> getAllMarketPlaceItems() {
        List<ItemMarketPlaceDTO> items = itemMarketPlaceService.getAllMarketPlaceItems();
        return ResponseEntity.ok(items);
    }

    /**
     * Returns marketplace items for a given category.
     * @param categoryId the category id
     * @return ResponseEntity with list of ItemMarketPlaceDTO
     */
    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<List<ItemMarketPlaceDTO>> getMarketPlaceItemsByCategory(
            @PathVariable Long categoryId) {
        List<ItemMarketPlaceDTO> items = itemMarketPlaceService.getMarketPlaceItemsByCategory(categoryId);
        return ResponseEntity.ok(items);
    }
}
