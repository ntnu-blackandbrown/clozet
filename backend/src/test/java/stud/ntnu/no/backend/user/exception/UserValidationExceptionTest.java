package stud.ntnu.no.backend.user.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class UserValidationExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    UserValidationException exception = new UserValidationException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "Username cannot be empty";

    // Act
    UserValidationException exception = new UserValidationException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
