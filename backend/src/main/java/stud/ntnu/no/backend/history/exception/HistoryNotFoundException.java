package stud.ntnu.no.backend.history.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a history record is not found.
 */
public class HistoryNotFoundException extends BaseException {
    public HistoryNotFoundException(String message) {
        super(message);
    }
}
