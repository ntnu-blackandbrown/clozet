package stud.ntnu.no.backend.item.exception;

import stud.ntnu.no.backend.common.exception.BaseException;
import org.springframework.http.HttpStatus;
public class ItemValidationException extends BaseException {
    public ItemValidationException(String message) {
        super(message);
    }
}
