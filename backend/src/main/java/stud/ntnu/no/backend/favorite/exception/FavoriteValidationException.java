package stud.ntnu.no.backend.favorite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is a validation error related to favorites.
 * Responds with HTTP status BAD_REQUEST.
 */
public class FavoriteValidationException extends RuntimeException {
    /**
     * Constructs a new FavoriteValidationException with the specified detail message.
     *
     * @param message The detail message explaining the validation error
     */
    public FavoriteValidationException(String message) {
        super(message);
    }
}
