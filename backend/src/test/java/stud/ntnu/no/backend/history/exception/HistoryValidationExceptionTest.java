package stud.ntnu.no.backend.history.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class HistoryValidationExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        HistoryValidationException exception = new HistoryValidationException("Test message");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        String errorMessage = "User ID cannot be null";
        
        // Act
        HistoryValidationException exception = new HistoryValidationException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
} 