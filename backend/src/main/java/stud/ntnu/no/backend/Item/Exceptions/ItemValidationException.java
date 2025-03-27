package stud.ntnu.no.backend.Item.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemValidationException extends RuntimeException {
    public ItemValidationException(String message) {
        super(message);
    }
}