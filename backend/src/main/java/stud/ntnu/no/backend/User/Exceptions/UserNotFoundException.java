package stud.ntnu.no.backend.User.Exceptions;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(Long id) {
    super("User not found with id: " + id);
  }
}