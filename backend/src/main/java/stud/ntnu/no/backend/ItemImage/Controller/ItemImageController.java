package stud.ntnu.no.backend.ItemImage.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.ItemImage.DTOs.CreateItemImageDTO;
import stud.ntnu.no.backend.ItemImage.DTOs.ItemImageDTO;
import stud.ntnu.no.backend.ItemImage.Service.ItemImageService;

import java.util.List;

@RestController
@RequestMapping("/api/item-images")
public class ItemImageController {

    private final ItemImageService itemImageService;

    public ItemImageController(ItemImageService itemImageService) {
        this.itemImageService = itemImageService;
    }

    @GetMapping
    public ResponseEntity<List<ItemImageDTO>> getAllItemImages() {
        return ResponseEntity.ok(itemImageService.getAllItemImages());
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ItemImageDTO>> getItemImagesByItemId(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemImageService.getItemImagesByItemId(itemId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemImageDTO> getItemImage(@PathVariable Long id) {
        return ResponseEntity.ok(itemImageService.getItemImage(id));
    }

    @PostMapping
    public ResponseEntity<ItemImageDTO> createItemImage(@RequestBody CreateItemImageDTO itemImageDTO) {
        return new ResponseEntity<>(itemImageService.createItemImage(itemImageDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemImageDTO> updateItemImage(@PathVariable Long id, @RequestBody CreateItemImageDTO itemImageDTO) {
        return ResponseEntity.ok(itemImageService.updateItemImage(id, itemImageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemImage(@PathVariable Long id) {
        itemImageService.deleteItemImage(id);
        return ResponseEntity.noContent().build();
    }
}