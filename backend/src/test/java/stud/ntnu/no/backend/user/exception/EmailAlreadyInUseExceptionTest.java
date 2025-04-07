package stud.ntnu.no.backend.user.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class EmailAlreadyInUseExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        EmailAlreadyInUseException exception = new EmailAlreadyInUseException("test@example.com");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        String email = "test@example.com";
        
        // Act
        EmailAlreadyInUseException exception = new EmailAlreadyInUseException(email);
        
        // Assert
        assertEquals("Email already in use: " + email, exception.getMessage());
    }
} 