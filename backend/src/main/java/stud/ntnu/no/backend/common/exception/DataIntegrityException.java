package stud.ntnu.no.backend.common.exception;

/**
 * Exception thrown when a data integrity violation occurs.
 */
public class DataIntegrityException extends RuntimeException {

    /**
     * Constructs a {@code DataIntegrityException} with the specified message.
     *
     * @param message the detail message
     */
    public DataIntegrityException(String message) {
        super(message);
    }
    
    /**
     * Constructs a {@code DataIntegrityException} with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}