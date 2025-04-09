package stud.ntnu.no.backend.favorite.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.service.FavoriteService;

/**
 * REST controller for handling favorite operations. Exposes endpoints to retrieve, create, update,
 * and delete favorites.
 */
@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

  private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

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
    logger.info("Received request to get all favorites.");
    List<FavoriteDTO> favorites = favoriteService.getAllFavorites();
    logger.info("Returning {} favorites.", favorites.size());
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
    logger.info("Received request to get favorite by ID: {}", id);
    FavoriteDTO favorite = favoriteService.getFavoriteById(id);
    logger.info("Returning favorite: {}", favorite);
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
    logger.info("Received request to get favorites by user ID: {}", userId);
    List<FavoriteDTO> favorites = favoriteService.getFavoritesByUserId(userId);
    logger.info("Returning {} favorites for user ID: {}", favorites.size(), userId);
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
    logger.info("Received request to create a new favorite: {}", request);
    FavoriteDTO createdFavorite = favoriteService.createFavorite(request);
    logger.info("Created favorite: {}", createdFavorite);
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
  public ResponseEntity<FavoriteDTO> updateFavorite(
      @PathVariable Long id, @RequestBody CreateFavoriteRequest request) {
    logger.info("Received request to update favorite with ID: {}, request: {}", id, request);
    FavoriteDTO updatedFavorite = favoriteService.updateFavorite(id, request);
    logger.info("Updated favorite: {}", updatedFavorite);
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
    logger.info("Received request to delete favorite with ID: {}", id);
    favoriteService.deleteFavorite(id);
    logger.info("Deleted favorite with ID: {}", id);
    return ResponseEntity.noContent().build();
  }
}
