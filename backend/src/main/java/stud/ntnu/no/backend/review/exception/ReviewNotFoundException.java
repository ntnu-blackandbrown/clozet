package stud.ntnu.no.backend.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a review is not found.
 * <p>
 * This exception is mapped to a 404 Not Found HTTP status code.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReviewNotFoundException extends RuntimeException {
    /**
     * Constructs a new ReviewNotFoundException with the specified review ID.
     *
     * @param id the ID of the review that was not found
     */
    public ReviewNotFoundException(Long id) {
        super("Could not find review with id: " + id);
    }

    /**
     * Constructs a new ReviewNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ReviewNotFoundException(String message) {
        super(message);
    }
}