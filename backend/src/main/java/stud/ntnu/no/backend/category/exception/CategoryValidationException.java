package stud.ntnu.no.backend.category.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when category validation fails.
 * Returns HTTP 400 Bad Request response to the client.
 */
public class CategoryValidationException extends BaseException {
    /**
     * Constructs the exception with a message describing the validation error.
     *
     * @param message Description of the validation error
     */
    public CategoryValidationException(String message) {
        super(message);
    }
}
