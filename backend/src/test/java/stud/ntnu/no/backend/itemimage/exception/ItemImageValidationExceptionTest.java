package stud.ntnu.no.backend.itemimage.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class ItemImageValidationExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    ItemImageValidationException exception = new ItemImageValidationException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "Item image description cannot exceed 255 characters";

    // Act
    ItemImageValidationException exception = new ItemImageValidationException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
