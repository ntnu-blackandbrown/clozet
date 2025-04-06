// MessageNotFoundException.java
package stud.ntnu.no.backend.message.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a message is not found.
 * <p>
 * This exception is mapped to a 404 Not Found HTTP status code.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends RuntimeException {
    /**
     * Constructs a new MessageNotFoundException with the specified message ID.
     *
     * @param id the ID of the message that was not found
     */
    public MessageNotFoundException(Long id) {
        super("Message not found with id: " + id);
    }
}