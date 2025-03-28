package stud.ntnu.no.backend.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReviewValidationException extends RuntimeException {
    public ReviewValidationException(String message) {
        super(message);
    }
}