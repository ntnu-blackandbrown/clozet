package stud.ntnu.no.backend.user.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/**
 * Exception thrown when an email is already in use.
 */
public class EmailAlreadyInUseException extends BaseException {
  public EmailAlreadyInUseException(String email) {
    super("Email already in use: " + email);
  }
}
