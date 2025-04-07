package stud.ntnu.no.backend.shippingoption.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class ShippingOptionValidationExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        ShippingOptionValidationException exception = new ShippingOptionValidationException("Test message");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        String errorMessage = "Shipping option name cannot be empty";
        
        // Act
        ShippingOptionValidationException exception = new ShippingOptionValidationException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
} 