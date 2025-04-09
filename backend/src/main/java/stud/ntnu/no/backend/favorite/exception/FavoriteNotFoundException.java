package stud.ntnu.no.backend.favorite.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a favorite is not found. Responds with HTTP status NOT_FOUND.
 */
public class FavoriteNotFoundException extends BaseException {

  /**
   * Constructs a new FavoriteNotFoundException with the specified favorite ID.
   *
   * @param id The ID of the favorite that was not found
   */
  public FavoriteNotFoundException(Long id) {
    super("Favorite not found with id: " + id);
  }
}
