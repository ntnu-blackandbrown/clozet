package stud.ntnu.no.backend.location.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class LocationNotFoundExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        LocationNotFoundException exception = new LocationNotFoundException(1L);
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        Long locationId = 123L;
        
        // Act
        LocationNotFoundException exception = new LocationNotFoundException(locationId);
        
        // Assert
        assertEquals("Could not find location with id: " + locationId, exception.getMessage());
    }
} 