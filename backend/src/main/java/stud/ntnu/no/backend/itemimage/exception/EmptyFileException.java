package stud.ntnu.no.backend.itemimage.exception;

/**
 * Exception thrown when an attempt is made to process an empty file.
 */
public class EmptyFileException extends RuntimeException {
    /**
     * Constructs a new EmptyFileException with the specified detail message.
     *
     * @param message The detail message explaining the exception
     */
    public EmptyFileException(String message) {
        super(message);
    }
}
