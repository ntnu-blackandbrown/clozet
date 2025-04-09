package stud.ntnu.no.backend.user.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class AuthenticationExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    AuthenticationException exception = new AuthenticationException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "Invalid username or password";

    // Act
    AuthenticationException exception = new AuthenticationException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
