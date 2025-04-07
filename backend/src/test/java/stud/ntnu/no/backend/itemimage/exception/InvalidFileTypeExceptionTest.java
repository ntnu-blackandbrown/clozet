package stud.ntnu.no.backend.itemimage.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class InvalidFileTypeExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        InvalidFileTypeException exception = new InvalidFileTypeException("Test message");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        String errorMessage = "File type is not supported. Only JPEG, PNG, and GIF are allowed.";
        
        // Act
        InvalidFileTypeException exception = new InvalidFileTypeException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
} 