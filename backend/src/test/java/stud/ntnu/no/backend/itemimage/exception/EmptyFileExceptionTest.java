package stud.ntnu.no.backend.itemimage.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class EmptyFileExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        EmptyFileException exception = new EmptyFileException("Test message");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        String errorMessage = "File is empty";
        
        // Act
        EmptyFileException exception = new EmptyFileException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
} 