package stud.ntnu.no.backend.transaction.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class TransactionNotFoundExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        TransactionNotFoundException exception = new TransactionNotFoundException("Test message");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldConstructWithId() {
        // Arrange
        Long transactionId = 123L;
        
        // Act
        TransactionNotFoundException exception = new TransactionNotFoundException(transactionId);
        
        // Assert
        assertEquals("Transaction not found with id: " + transactionId, exception.getMessage());
    }

    @Test
    void shouldConstructWithMessage() {
        // Arrange
        String errorMessage = "Custom transaction not found message";
        
        // Act
        TransactionNotFoundException exception = new TransactionNotFoundException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
} 