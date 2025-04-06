package stud.ntnu.no.backend.itemimage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is a validation error related to item images.
 * Responds with HTTP status BAD_REQUEST.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemImageValidationException extends RuntimeException {
    /**
     * Constructs a new ItemImageValidationException with the specified detail message.
     *
     * @param message The detail message explaining the validation error
     */
    public ItemImageValidationException(String message) {
        super(message);
    }
}