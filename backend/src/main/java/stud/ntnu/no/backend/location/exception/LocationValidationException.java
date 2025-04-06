package stud.ntnu.no.backend.location.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a location validation fails.
 * <p>
 * This exception is mapped to a 400 Bad Request HTTP status code.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LocationValidationException extends BaseException {
    /**
     * Constructs a new LocationValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public LocationValidationException(String message) {
        super(message);
    }
}
