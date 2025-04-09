package stud.ntnu.no.backend.message.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class MessageNotFoundExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    MessageNotFoundException exception = new MessageNotFoundException(1L);

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    Long messageId = 123L;

    // Act
    MessageNotFoundException exception = new MessageNotFoundException(messageId);

    // Assert
    assertEquals("Message not found with id: " + messageId, exception.getMessage());
  }
}
