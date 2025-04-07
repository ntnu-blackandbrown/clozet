package stud.ntnu.no.backend.itemimage.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class ItemImageValidationExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        ItemImageValidationException exception = new ItemImageValidationException("Test message");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        String errorMessage = "Item image description cannot exceed 255 characters";
        
        // Act
        ItemImageValidationException exception = new ItemImageValidationException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
} 