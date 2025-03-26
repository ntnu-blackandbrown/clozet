package stud.ntnu.no.backend.Category.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryValidationException extends RuntimeException {
    public CategoryValidationException(String message) {
        super(message);
    }
}