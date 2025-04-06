package stud.ntnu.no.backend.history.exception;

/**
 * Exception thrown when there is a validation error with a history record.
 */
public class HistoryValidationException extends RuntimeException {
    public HistoryValidationException(String message) {
        super(message);
    }
}
