package stud.ntnu.no.backend.common.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidDataExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    InvalidDataException exception = new InvalidDataException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "The provided data is invalid";

    // Act
    InvalidDataException exception = new InvalidDataException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
