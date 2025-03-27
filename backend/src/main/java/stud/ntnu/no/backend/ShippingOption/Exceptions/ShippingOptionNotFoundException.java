package stud.ntnu.no.backend.ShippingOption.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShippingOptionNotFoundException extends RuntimeException {
    public ShippingOptionNotFoundException(Long id) {
        super("Could not find shipping option with id: " + id);
    }
}