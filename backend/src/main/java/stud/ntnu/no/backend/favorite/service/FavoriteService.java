package stud.ntnu.no.backend.favorite.service;

import java.util.List;
import stud.ntnu.no.backend.favorite.dto.CreateFavoriteRequest;
import stud.ntnu.no.backend.favorite.dto.FavoriteDTO;
import stud.ntnu.no.backend.favorite.exception.FavoriteNotFoundException;
import stud.ntnu.no.backend.favorite.exception.FavoriteValidationException;

/**
 * Service interface for managing favorites. Defines operations for retrieving, creating, updating,
 * and deleting favorites.
 */
public interface FavoriteService {

  /**
   * Retrieves all favorites in the system.
   *
   * @return A list of all favorites as FavoriteDTO objects
   */
  List<FavoriteDTO> getAllFavorites();

  /**
   * Retrieves all favorites for a specific user.
   *
   * @param userId The ID of the user
   * @return A list of the user's favorites as FavoriteDTO objects
   * @throws FavoriteValidationException if the user ID is null or empty
   */
  List<FavoriteDTO> getFavoritesByUserId(String userId);

  /**
   * Retrieves a specific favorite by ID.
   *
   * @param id The ID of the favorite
   * @return FavoriteDTO object for the specific favorite
   * @throws FavoriteValidationException if the ID is null
   * @throws FavoriteNotFoundException if the favorite is not found
   */
  FavoriteDTO getFavoriteById(Long id);

  /**
   * Creates a new favorite.
   *
   * @param request Request with data for the new favorite
   * @return FavoriteDTO object for the new favorite
   * @throws FavoriteValidationException if the request is invalid or the favorite already exists
   */
  FavoriteDTO createFavorite(CreateFavoriteRequest request);

  /**
   * Updates an existing favorite.
   *
   * @param id The ID of the favorite
   * @param request Request with updated data
   * @return FavoriteDTO object for the updated favorite
   * @throws FavoriteValidationException if the ID or request is invalid
   * @throws FavoriteNotFoundException if the favorite is not found
   */
  FavoriteDTO updateFavorite(Long id, CreateFavoriteRequest request);

  /**
   * Deletes a favorite.
   *
   * @param id The ID of the favorite
   * @throws FavoriteValidationException if the ID is null
   * @throws FavoriteNotFoundException if the favorite is not found
   */
  void deleteFavorite(Long id);
}
