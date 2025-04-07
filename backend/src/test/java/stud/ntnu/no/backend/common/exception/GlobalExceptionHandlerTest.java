package stud.ntnu.no.backend.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import stud.ntnu.no.backend.category.exception.CategoryNotFoundException;
import stud.ntnu.no.backend.category.exception.CategoryValidationException;
import stud.ntnu.no.backend.favorite.exception.FavoriteNotFoundException;
import stud.ntnu.no.backend.favorite.exception.FavoriteValidationException;
import stud.ntnu.no.backend.item.exception.ItemNotFoundException;
import stud.ntnu.no.backend.item.exception.ItemValidationException;
import stud.ntnu.no.backend.user.exception.AuthenticationException;
import stud.ntnu.no.backend.user.exception.EmailAlreadyInUseException;
import stud.ntnu.no.backend.user.exception.UserNotFoundException;
import stud.ntnu.no.backend.user.exception.UsernameAlreadyExistsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleDataIntegrityViolationException() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Database error");
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleDataIntegrityViolationException(exception);
        
        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Database integrity constraint violated. Make sure to delete dependent records first.", errorResponse.getMessage());
    }

    @Test
    void shouldHandleCustomDataIntegrityException() {
        // Arrange
        String errorMessage = "Custom integrity error";
        DataIntegrityException exception = new DataIntegrityException(errorMessage);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleCustomDataIntegrityException(exception);
        
        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @Test
    void shouldHandleCategoryNotFoundException() {
        // Arrange
        String errorMessage = "Category not found";
        CategoryNotFoundException exception = new CategoryNotFoundException(errorMessage);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleCategoryNotFoundException(exception);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @Test
    void shouldHandleCategoryValidationException() {
        // Arrange
        String errorMessage = "Invalid category";
        CategoryValidationException exception = new CategoryValidationException(errorMessage);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleCategoryValidationException(exception);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @Test
    void shouldHandleItemNotFoundException() {
        // Arrange
        String errorMessage = "Item not found";
        ItemNotFoundException exception = new ItemNotFoundException(errorMessage);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleItemNotFoundException(exception);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @Test
    void shouldHandleItemValidationException() {
        // Arrange
        String errorMessage = "Invalid item";
        ItemValidationException exception = new ItemValidationException(errorMessage);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleItemValidationException(exception);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @Test
    void shouldHandleAuthenticationException() {
        // Arrange
        String errorMessage = "Authentication failed";
        AuthenticationException exception = new AuthenticationException(errorMessage);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleAuthenticationException(exception);
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @Test
    void shouldHandleEmailAlreadyInUseException() {
        // Arrange
        String email = "test@example.com";
        EmailAlreadyInUseException exception = new EmailAlreadyInUseException(email);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleEmailAlreadyInUseException(exception);
        
        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Email already in use: " + email, errorResponse.getMessage());
    }

    @Test
    void shouldHandleInvalidDataException() {
        // Arrange
        String errorMessage = "Invalid data";
        InvalidDataException exception = new InvalidDataException(errorMessage);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleInvalidDataException(exception);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @Test
    void shouldHandleFavoriteNotFoundException() {
        // Arrange
        Long favoriteId = 123L;
        FavoriteNotFoundException exception = new FavoriteNotFoundException(favoriteId);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleFavoriteNotFoundException(exception);
        
        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Favorite not found with id: " + favoriteId, errorResponse.getMessage());
    }

    @Test
    void shouldHandleFavoriteValidationException() {
        // Arrange
        String errorMessage = "Invalid favorite";
        FavoriteValidationException exception = new FavoriteValidationException(errorMessage);
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleFavoriteValidationException(exception);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @Test
    void shouldHandleGenericException() {
        // Arrange
        Exception exception = new Exception("Unexpected error");
        
        // Act
        ResponseEntity<Object> response = exceptionHandler.handleAllUncaughtException(exception);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("An unexpected error occurred.", errorResponse.getMessage());
    }
} 