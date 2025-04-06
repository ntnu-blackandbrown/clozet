package stud.ntnu.no.backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when invalid data is encountered.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDataException extends RuntimeException {

    /**
     * Constructs an {@code InvalidDataException} with the specified message.
     *
     * @param message the detail message
     */
    public InvalidDataException(String message) {
        super(message);
    }
}