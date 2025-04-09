package stud.ntnu.no.backend.favorite.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class FavoriteValidationExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    FavoriteValidationException exception = new FavoriteValidationException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "User ID cannot be null or empty";

    // Act
    FavoriteValidationException exception = new FavoriteValidationException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
