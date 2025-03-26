package stud.ntnu.no.backend.User.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserValidationException extends RuntimeException {
    public UserValidationException(String message) {
        super(message);
    }
}