package stud.ntnu.no.backend.shippingoption.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a shipping option is not found.
 * <p>
 * This exception is mapped to a 404 Not Found HTTP status code.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShippingOptionNotFoundException extends RuntimeException {
    /**
     * Constructs a new ShippingOptionNotFoundException with the specified shipping option ID.
     *
     * @param id the ID of the shipping option that was not found
     */
    public ShippingOptionNotFoundException(Long id) {
        super("Could not find shipping option with id: " + id);
    }
}