package stud.ntnu.no.backend.user.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/** Exception thrown when authentication fails. */
public class AuthenticationException extends BaseException {

  public AuthenticationException(String message) {
    super(message);
  }
}
