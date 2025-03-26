package stud.ntnu.no.backend.Review.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long id) {
        super("Could not find review with id: " + id);
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }
}