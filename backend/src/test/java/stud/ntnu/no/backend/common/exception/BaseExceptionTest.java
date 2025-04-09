package stud.ntnu.no.backend.common.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BaseExceptionTest {

  @Test
  void shouldExtendRuntimeException() {
    // Arrange & Act
    BaseException exception = new BaseException("Test message");

    // Assert
    assertTrue(exception instanceof RuntimeException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "This is a test error message";

    // Act
    BaseException exception = new BaseException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
