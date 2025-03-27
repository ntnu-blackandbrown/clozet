package stud.ntnu.no.backend.ShippingOption.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ShippingOptionValidationException extends RuntimeException {
    public ShippingOptionValidationException(String message) {
        super(message);
    }
}