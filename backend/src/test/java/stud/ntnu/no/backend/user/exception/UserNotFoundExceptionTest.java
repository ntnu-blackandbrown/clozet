package stud.ntnu.no.backend.user.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class UserNotFoundExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    UserNotFoundException exception = new UserNotFoundException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldConstructWithId() {
    // Arrange
    Long userId = 123L;

    // Act
    UserNotFoundException exception = new UserNotFoundException(userId);

    // Assert
    assertEquals("User not found with id: " + userId, exception.getMessage());
  }

  @Test
  void shouldConstructWithMessage() {
    // Arrange
    String errorMessage = "User not found with email: test@example.com";

    // Act
    UserNotFoundException exception = new UserNotFoundException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
