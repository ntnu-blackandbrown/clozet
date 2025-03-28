package stud.ntnu.no.backend.itemimage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemImageNotFoundException extends RuntimeException {
    public ItemImageNotFoundException(Long id) {
        super("Could not find item image with id: " + id);
    }
}