package stud.ntnu.no.backend.user.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends BaseException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
    
    public UserNotFoundException(String message) {
        super(message);
    }
}
