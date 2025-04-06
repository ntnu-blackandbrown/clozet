// FavoriteNotFoundException.java
package stud.ntnu.no.backend.favorite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a favorite is not found.
 * Responds with HTTP status NOT_FOUND.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FavoriteNotFoundException extends RuntimeException {
    /**
     * Constructs a new FavoriteNotFoundException with the specified favorite ID.
     *
     * @param id The ID of the favorite that was not found
     */
    public FavoriteNotFoundException(Long id) {
        super("Favorite not found with id: " + id);
    }
}