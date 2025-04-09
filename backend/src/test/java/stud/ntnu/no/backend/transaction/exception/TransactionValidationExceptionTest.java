package stud.ntnu.no.backend.transaction.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class TransactionValidationExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    TransactionValidationException exception = new TransactionValidationException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    String errorMessage = "Transaction amount cannot be negative";

    // Act
    TransactionValidationException exception = new TransactionValidationException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
