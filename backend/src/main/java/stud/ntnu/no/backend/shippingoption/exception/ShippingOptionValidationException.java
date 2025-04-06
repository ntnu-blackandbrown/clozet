package stud.ntnu.no.backend.shippingoption.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a shipping option validation fails.
 * <p>
 * This exception is mapped to a 400 Bad Request HTTP status code.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ShippingOptionValidationException extends RuntimeException {
    /**
     * Constructs a new ShippingOptionValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ShippingOptionValidationException(String message) {
        super(message);
    }
}