package stud.ntnu.no.backend.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidDataExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        InvalidDataException exception = new InvalidDataException("Test message");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        String errorMessage = "The provided data is invalid";
        
        // Act
        InvalidDataException exception = new InvalidDataException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
} 