package stud.ntnu.no.backend.favorite.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteNotFoundExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        FavoriteNotFoundException exception = new FavoriteNotFoundException(1L);
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldContainCorrectErrorMessage() {
        // Arrange
        Long favoriteId = 123L;
        
        // Act
        FavoriteNotFoundException exception = new FavoriteNotFoundException(favoriteId);
        
        // Assert
        assertEquals("Favorite not found with id: " + favoriteId, exception.getMessage());
    }
} 