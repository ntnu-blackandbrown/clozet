package stud.ntnu.no.backend.User.Exceptions;

public class EmailAlreadyInUseException extends RuntimeException {
  public EmailAlreadyInUseException(String email) {
    super("Email already in use: " + email);
  }
}
