package stud.ntnu.no.backend.review.exception;

import stud.ntnu.no.backend.common.exception.BaseException;
/**
 * Exception thrown when a review validation fails.
 * <p>
 * This exception is mapped to a 400 Bad Request HTTP status code.
 */
public class ReviewValidationException extends BaseException {
    /**
     * Constructs a new ReviewValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ReviewValidationException(String message) {
        super(message);
    }
}
