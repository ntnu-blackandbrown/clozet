package stud.ntnu.no.backend.itemimage.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when there is a validation error related to item images.
 * Responds with HTTP status BAD_REQUEST.
 */
public class ItemImageValidationException extends BaseException {
    /**
     * Constructs a new ItemImageValidationException with the specified detail message.
     *
     * @param message The detail message explaining the validation error
     */
    public ItemImageValidationException(String message) {
        super(message);
    }
}
