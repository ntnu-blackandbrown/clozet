// FavoriteNotFoundException.java
package stud.ntnu.no.backend.favorite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(Long id) {
        super("Favorite not found with id: " + id);
    }
}