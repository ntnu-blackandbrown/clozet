package stud.ntnu.no.backend.category.exception;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.common.exception.BaseException;

import static org.junit.jupiter.api.Assertions.*;

class CategoryNotFoundExceptionTest {

    @Test
    void shouldExtendBaseException() {
        // Arrange & Act
        CategoryNotFoundException exception = new CategoryNotFoundException("Test message");
        
        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void shouldConstructWithId() {
        // Arrange
        Long categoryId = 123L;
        
        // Act
        CategoryNotFoundException exception = new CategoryNotFoundException(categoryId);
        
        // Assert
        assertEquals("Could not find category with id: " + categoryId, exception.getMessage());
    }

    @Test
    void shouldConstructWithMessage() {
        // Arrange
        String errorMessage = "Category not found with name: Test";
        
        // Act
        CategoryNotFoundException exception = new CategoryNotFoundException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
}