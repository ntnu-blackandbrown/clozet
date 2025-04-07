package stud.ntnu.no.backend.review.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class ReviewValidationExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        ReviewValidationException exception = new ReviewValidationException("Test message");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        String errorMessage = "Review rating must be between 1 and 5";
        
        // Act
        ReviewValidationException exception = new ReviewValidationException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
} 