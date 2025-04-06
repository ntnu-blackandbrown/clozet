package stud.ntnu.no.backend.location.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a location is not found.
 * <p>
 * This exception is mapped to a 404 Not Found HTTP status code.
 */
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
