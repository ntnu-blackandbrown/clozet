package stud.ntnu.no.backend.location.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class LocationValidationExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    LocationValidationException exception = new LocationValidationException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "Location name cannot be empty";

    // Act
    LocationValidationException exception = new LocationValidationException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
