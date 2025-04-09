package stud.ntnu.no.backend.itemimage.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class ItemImageNotFoundExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    ItemImageNotFoundException exception = new ItemImageNotFoundException(1L);

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    Long imageId = 123L;

    // Act
    ItemImageNotFoundException exception = new ItemImageNotFoundException(imageId);

    // Assert
    assertEquals("Could not find item image with id: " + imageId, exception.getMessage());
  }
}
