package stud.ntnu.no.backend.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an email is already in use.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyInUseException extends RuntimeException {
  public EmailAlreadyInUseException(String email) {
    super("Email already in use: " + email);
  }
}