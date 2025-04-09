package stud.ntnu.no.backend.item.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class ItemValidationExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    ItemValidationException exception = new ItemValidationException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "Item name cannot be empty";

    // Act
    ItemValidationException exception = new ItemValidationException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
