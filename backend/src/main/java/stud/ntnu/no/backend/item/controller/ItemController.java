package stud.ntnu.no.backend.item.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import stud.ntnu.no.backend.common.security.model.CustomUserDetails;
import stud.ntnu.no.backend.item.service.ItemService;
import stud.ntnu.no.backend.item.dto.CreateItemDTO;
import stud.ntnu.no.backend.item.dto.ItemDTO;
import stud.ntnu.no.backend.user.entity.User;

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
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        return new ResponseEntity<>(itemService.createItem(itemDTO, user.getId()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody CreateItemDTO itemDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        return ResponseEntity.ok(itemService.updateItem(id, itemDTO, user.getId()));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateItem(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        itemService.deactivateItem(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateItem(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        itemService.activateItem(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        itemService.deleteItem(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    public Object getUserId() {
        return null;
    }
}