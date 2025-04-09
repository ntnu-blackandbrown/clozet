package stud.ntnu.no.backend.category.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class CategoryValidationExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    CategoryValidationException exception = new CategoryValidationException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "Category name cannot be empty";

    // Act
    CategoryValidationException exception = new CategoryValidationException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
