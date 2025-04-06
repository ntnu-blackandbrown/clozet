package stud.ntnu.no.backend.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a review validation fails.
 * <p>
 * This exception is mapped to a 400 Bad Request HTTP status code.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReviewValidationException extends RuntimeException {
    /**
     * Constructs a new ReviewValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ReviewValidationException(String message) {
        super(message);
    }
}