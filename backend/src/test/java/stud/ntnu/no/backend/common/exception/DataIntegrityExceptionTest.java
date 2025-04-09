package stud.ntnu.no.backend.common.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DataIntegrityExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    DataIntegrityException exception = new DataIntegrityException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessageWithMessageConstructor() {
    // Arrange
    String errorMessage = "Unique constraint violation";

    // Act
    DataIntegrityException exception = new DataIntegrityException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }

  @Test
  void shouldContainCauseInformationWithMessageAndCauseConstructor() {
    // Arrange
    String errorMessage = "Unique constraint violation";
    String causeMessage = "Duplicate key value";
    Throwable cause = new RuntimeException(causeMessage);

    // Act
    DataIntegrityException exception = new DataIntegrityException(errorMessage, cause);

    // Assert
    assertEquals(errorMessage + " Cause: " + causeMessage, exception.getMessage());
  }
}
