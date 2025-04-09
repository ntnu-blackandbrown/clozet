package stud.ntnu.no.backend.user.exception;

import stud.ntnu.no.backend.common.exception.BaseException;

/** Exception thrown when a username already exists. */
public class UsernameAlreadyExistsException extends BaseException {

  public UsernameAlreadyExistsException(String username) {
    super("Username already exists: " + username);
  }
}
