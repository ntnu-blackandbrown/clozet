package stud.ntnu.no.backend.itemimage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an item image is not found.
 * Responds with HTTP status NOT_FOUND.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemImageNotFoundException extends RuntimeException {
    /**
     * Constructs a new ItemImageNotFoundException with the specified item image ID.
     *
     * @param id The ID of the item image that was not found
     */
    public ItemImageNotFoundException(Long id) {
        super("Could not find item image with id: " + id);
    }
}