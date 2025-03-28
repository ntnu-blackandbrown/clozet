package stud.ntnu.no.backend.location.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LocationValidationException extends RuntimeException {
    public LocationValidationException(String message) {
        super(message);
    }
}