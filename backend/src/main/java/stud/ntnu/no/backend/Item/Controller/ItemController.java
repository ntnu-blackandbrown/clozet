package stud.ntnu.no.backend.Item.Controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.Item.DTOs.CreateItemDTO;
import stud.ntnu.no.backend.Item.DTOs.ItemDTO;
import stud.ntnu.no.backend.Item.Service.ItemService;
import stud.ntnu.no.backend.User.Entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        return ResponseEntity.ok(itemService.getActiveItems());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemDTO>> getAllItemsIncludingInactive() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItem(id));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ItemDTO>> getItemsBySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(itemService.getItemsBySeller(sellerId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ItemDTO>> getItemsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(itemService.getItemsByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDTO>> searchItems(@RequestParam String query) {
        return ResponseEntity.ok(itemService.searchItems(query));
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody CreateItemDTO itemDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return new ResponseEntity<>(itemService.createItem(itemDTO, user.getId()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody CreateItemDTO itemDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(itemService.updateItem(id, itemDTO, user.getId()));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateItem(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        itemService.deactivateItem(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateItem(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        itemService.activateItem(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        itemService.deleteItem(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}