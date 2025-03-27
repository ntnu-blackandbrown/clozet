package stud.ntnu.no.backend.Favorite.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.Favorite.DTOs.CreateFavoriteRequest;
import stud.ntnu.no.backend.Favorite.DTOs.FavoriteDTO;
import stud.ntnu.no.backend.Favorite.DTOs.UpdateFavoriteRequest;
import stud.ntnu.no.backend.Favorite.Service.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getAllFavorites() {
        return ResponseEntity.ok(favoriteService.getAllFavorites());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FavoriteDTO> getFavoriteById(@PathVariable Long id) {
        return ResponseEntity.ok(favoriteService.getFavoriteById(id));
    }

    @PostMapping
    public ResponseEntity<FavoriteDTO> createFavorite(@RequestBody CreateFavoriteRequest request) {
        return new ResponseEntity<>(favoriteService.createFavorite(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FavoriteDTO> updateFavorite(
            @PathVariable Long id,
            @RequestBody UpdateFavoriteRequest request) {
        return ResponseEntity.ok(favoriteService.updateFavorite(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}