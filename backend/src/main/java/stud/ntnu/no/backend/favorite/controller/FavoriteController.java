package stud.ntnu.no.backend.favorite.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.service.FavoriteService;

import java.util.List;

/**
 * REST controller for handling favorite operations.
 * Exposes endpoints to retrieve, create, update, and delete favorites.
 */
@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * Retrieves all favorites.
     *
     * @return List of all favorites
     */
    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getAllFavorites() {
        List<FavoriteDTO> favorites = favoriteService.getAllFavorites();
        return ResponseEntity.ok(favorites);
    }

    /**
     * Retrieves a specific favorite by ID.
     *
     * @param id The ID of the favorite
     * @return The favorite with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FavoriteDTO> getFavoriteById(@PathVariable Long id) {
        FavoriteDTO favorite = favoriteService.getFavoriteById(id);
        return ResponseEntity.ok(favorite);
    }

    /**
     * Retrieves all favorites for a specific user.
     *
     * @param userId The ID of the user
     * @return List of the user's favorites
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByUserId(@PathVariable String userId) {
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }

    /**
     * Creates a new favorite.
     *
     * @param request Data for creating a new favorite
     * @return The newly created favorite
     */
    @PostMapping
    public ResponseEntity<FavoriteDTO> createFavorite(@RequestBody CreateFavoriteRequest request) {
        FavoriteDTO createdFavorite = favoriteService.createFavorite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFavorite);
    }

    /**
     * Updates an existing favorite.
     *
     * @param id The ID of the favorite
     * @param request Data for updating the favorite
     * @return The updated favorite
     */
    @PutMapping("/{id}")
    public ResponseEntity<FavoriteDTO> updateFavorite(@PathVariable Long id, @RequestBody CreateFavoriteRequest request) {
        FavoriteDTO updatedFavorite = favoriteService.updateFavorite(id, request);
        return ResponseEntity.ok(updatedFavorite);
    }

    /**
     * Deletes a favorite.
     *
     * @param id The ID of the favorite
     * @return Empty response with status NO_CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }
}