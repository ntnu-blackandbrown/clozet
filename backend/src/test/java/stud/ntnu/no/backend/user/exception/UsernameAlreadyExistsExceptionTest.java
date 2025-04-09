package stud.ntnu.no.backend.user.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class UsernameAlreadyExistsExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException("testuser");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String username = "testuser";

    // Act
    UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException(username);

    // Assert
    assertEquals("Username already exists: " + username, exception.getMessage());
  }
}
