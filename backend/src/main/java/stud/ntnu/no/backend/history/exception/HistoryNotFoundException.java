package stud.ntnu.no.backend.history.exception;

/**
 * Exception thrown when a history record is not found.
 */
public class HistoryNotFoundException extends RuntimeException {
    public HistoryNotFoundException(String message) {
        super(message);
    }
}
