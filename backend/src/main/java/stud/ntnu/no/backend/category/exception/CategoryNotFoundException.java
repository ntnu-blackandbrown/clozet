package stud.ntnu.no.backend.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested category cannot be found.
 * Returns HTTP 404 Not Found response to the client.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException {
    /**
     * Constructs the exception with an error message including the category ID.
     *
     * @param id ID of the category that was not found
     */
    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id);
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
