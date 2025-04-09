package stud.ntnu.no.backend.history.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class HistoryNotFoundExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    HistoryNotFoundException exception = new HistoryNotFoundException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "History record not found with id: 123";

    // Act
    HistoryNotFoundException exception = new HistoryNotFoundException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
