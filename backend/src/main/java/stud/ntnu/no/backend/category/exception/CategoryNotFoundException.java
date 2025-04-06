package stud.ntnu.no.backend.category.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a requested category cannot be found.
 * Returns HTTP 404 Not Found response to the client.
 */
public class CategoryNotFoundException extends BaseException {
    /**
     * Constructs the exception with an error message including the category ID.
     *
     * @param id ID of the category that was not found
     */
    public CategoryNotFoundException(Long id) {
        super("Could not find category with id: " + id);
    }

    /**
     * Constructs the exception with a custom error message.
     *
     * @param message Custom error message
     */
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
