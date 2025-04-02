package stud.ntnu.no.backend.user.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenTest {

    @Test
    void testCreateToken() {
        // Arrange
        User user = new User();
        String tokenValue = "test-token";
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        
        // Act
        PasswordResetToken token = new PasswordResetToken(tokenValue, expiryDate, user);
        
        // Assert
        assertEquals(tokenValue, token.getToken());
        assertEquals(expiryDate, token.getExpiryDate());
        assertEquals(user, token.getUser());
    }
    
    @Test
    void testIsExpired_whenTokenIsExpired_shouldReturnTrue() {
        // Arrange
        User user = new User();
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
        PasswordResetToken token = new PasswordResetToken("expired-token", pastTime, user);
        
        // Act & Assert
        assertTrue(token.isExpired());
    }
    
    @Test
    void testIsExpired_whenTokenIsValid_shouldReturnFalse() {
        // Arrange
        User user = new User();
        LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
        PasswordResetToken token = new PasswordResetToken("valid-token", futureTime, user);
        
        // Act & Assert
        assertFalse(token.isExpired());
    }
}