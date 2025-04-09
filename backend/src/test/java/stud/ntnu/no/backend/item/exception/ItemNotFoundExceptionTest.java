package stud.ntnu.no.backend.item.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class ItemNotFoundExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    ItemNotFoundException exception = new ItemNotFoundException("Test message");

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldConstructWithId() {
    // Arrange
    Long itemId = 123L;

    // Act
    ItemNotFoundException exception = new ItemNotFoundException(itemId);

    // Assert
    assertEquals("Item not found with id: " + itemId, exception.getMessage());
  }

  @Test
  void shouldConstructWithMessage() {
    // Arrange
    String errorMessage = "Custom item not found message";

    // Act
    ItemNotFoundException exception = new ItemNotFoundException(errorMessage);

    // Assert
    assertEquals(errorMessage, exception.getMessage());
  }
}
