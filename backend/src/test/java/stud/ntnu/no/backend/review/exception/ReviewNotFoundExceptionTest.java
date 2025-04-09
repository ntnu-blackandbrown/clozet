package stud.ntnu.no.backend.review.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class ReviewNotFoundExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    ReviewNotFoundException exception = new ReviewNotFoundException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "Review not found with id: 123";

    // Act
    ReviewNotFoundException exception = new ReviewNotFoundException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
