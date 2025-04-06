package stud.ntnu.no.backend.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when category validation fails.
 * Returns HTTP 400 Bad Request response to the client.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryValidationException extends RuntimeException {
    /**
     * Constructs the exception with a message describing the validation error.
     *
     * @param message Description of the validation error
     */
    public CategoryValidationException(String message) {
        super(message);
    }
}
