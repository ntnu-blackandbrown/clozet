package stud.ntnu.no.backend.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.item.dto.ItemMarketPlaceDTO;
import stud.ntnu.no.backend.item.service.ItemMarketPlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
public class ItemMarketPlaceController {

    private final ItemMarketPlaceService itemMarketPlaceService;

    @Autowired
    public ItemMarketPlaceController(ItemMarketPlaceService itemMarketPlaceService) {
        this.itemMarketPlaceService = itemMarketPlaceService;
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemMarketPlaceDTO>> getAllMarketPlaceItems() {
        List<ItemMarketPlaceDTO> items = itemMarketPlaceService.getAllMarketPlaceItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/category/{categoryId}")
    public ResponseEntity<List<ItemMarketPlaceDTO>> getMarketPlaceItemsByCategory(
            @PathVariable Long categoryId) {
        List<ItemMarketPlaceDTO> items = itemMarketPlaceService.getMarketPlaceItemsByCategory(categoryId);
        return ResponseEntity.ok(items);
    }
}