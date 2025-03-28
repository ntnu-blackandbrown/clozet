// src/main/java/stud/ntnu/no/backend/favorite/exception/FavoriteValidationException.java
package stud.ntnu.no.backend.favorite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FavoriteValidationException extends RuntimeException {
    public FavoriteValidationException(String message) {
        super(message);
    }
}