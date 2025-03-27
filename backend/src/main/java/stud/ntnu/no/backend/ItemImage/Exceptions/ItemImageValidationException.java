package stud.ntnu.no.backend.ItemImage.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemImageValidationException extends RuntimeException {
    public ItemImageValidationException(String message) {
        super(message);
    }
}