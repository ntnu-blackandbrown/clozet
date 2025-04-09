package stud.ntnu.no.backend.shippingoption.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

class ShippingOptionNotFoundExceptionTest {

  @Test
  void shouldExtendBaseException() {
    // Arrange & Act
    ShippingOptionNotFoundException exception = new ShippingOptionNotFoundException(1L);

    // Assert
    assertTrue(exception instanceof BaseException);
  }

  @Test
  void shouldContainCorrectErrorMessage() {
    // Arrange
    Long optionId = 123L;

    // Act
    ShippingOptionNotFoundException exception = new ShippingOptionNotFoundException(optionId);

    // Assert
    assertEquals("Could not find shipping option with id: " + optionId, exception.getMessage());
  }
}
