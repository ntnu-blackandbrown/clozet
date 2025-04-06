package stud.ntnu.no.backend.user.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when user validation fails.
 */
public class UserValidationException extends BaseException {
    public UserValidationException(String message) {
        super(message);
    }
}
