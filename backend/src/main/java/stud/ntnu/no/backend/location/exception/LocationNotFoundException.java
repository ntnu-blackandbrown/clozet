package stud.ntnu.no.backend.location.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a location is not found.
 * <p>
 * This exception is mapped to a 404 Not Found HTTP status code.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class LocationNotFoundException extends BaseException {
    /**
     * Constructs a new LocationNotFoundException with the specified location ID.
     *
     * @param id the ID of the location that was not found
     */
    public LocationNotFoundException(Long id) {
        super("Could not find location with id: " + id);
    }
}
