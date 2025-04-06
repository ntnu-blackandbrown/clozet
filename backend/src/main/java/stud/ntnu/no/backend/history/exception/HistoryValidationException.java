package stud.ntnu.no.backend.history.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when there is a validation error with a history record.
 */
public class HistoryValidationException extends BaseException {
    public HistoryValidationException(String message) {
        super(message);
    }
}
