package stud.ntnu.no.backend.common.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void shouldCreateErrorResponseWithConstructor() {
    // Arrange
    String message = "Error message";
    LocalDateTime timestamp = LocalDateTime.now();
    int status = 404;

    // Act
    ErrorResponse errorResponse = new ErrorResponse(message, timestamp, status);

    // Assert
    assertEquals(message, errorResponse.getMessage());
    assertEquals(timestamp, errorResponse.getTimestamp());
    assertEquals(status, errorResponse.getStatusCode());
  }

  @Test
  void shouldSetAndGetMessage() {
    // Arrange
    ErrorResponse errorResponse = new ErrorResponse("Old message", LocalDateTime.now(), 400);
    String newMessage = "New error message";

    // Act
    errorResponse.setMessage(newMessage);

    // Assert
    assertEquals(newMessage, errorResponse.getMessage());
  }

  @Test
  void shouldSetAndGetTimestamp() {
    // Arrange
    ErrorResponse errorResponse = new ErrorResponse("Error message", LocalDateTime.now(), 400);
    LocalDateTime newTimestamp = LocalDateTime.now().plusHours(1);

    // Act
    errorResponse.setTimestamp(newTimestamp);

    // Assert
    assertEquals(newTimestamp, errorResponse.getTimestamp());
  }

  @Test
  void shouldSetAndGetStatusCode() {
    // Arrange
    ErrorResponse errorResponse = new ErrorResponse("Error message", LocalDateTime.now(), 400);
    int newStatus = 500;

    // Act
    errorResponse.setStatusCode(newStatus);

    // Assert
    assertEquals(newStatus, errorResponse.getStatusCode());
  }
}
