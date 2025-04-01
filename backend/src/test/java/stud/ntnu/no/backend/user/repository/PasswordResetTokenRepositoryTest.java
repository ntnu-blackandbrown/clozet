package stud.ntnu.no.backend.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import stud.ntnu.no.backend.user.entity.PasswordResetToken;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PasswordResetTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindByToken_whenTokenExists_shouldReturnToken() {
        // Arrange
        User user = createTestUser();
        String tokenValue = "test-find-token";
        PasswordResetToken token = new PasswordResetToken(
                tokenValue, 
                LocalDateTime.now().plusHours(1), 
                user);
        
        entityManager.persist(token);
        entityManager.flush();
        
        // Act
        Optional<PasswordResetToken> foundToken = passwordResetTokenRepository.findByToken(tokenValue);
        
        // Assert
        assertTrue(foundToken.isPresent());
        assertEquals(tokenValue, foundToken.get().getToken());
    }
    
    @Test
    void testFindByToken_whenTokenDoesNotExist_shouldReturnEmpty() {
        // Act
        Optional<PasswordResetToken> foundToken = passwordResetTokenRepository.findByToken("non-existent-token");
        
        // Assert
        assertTrue(foundToken.isEmpty());
    }
    
    @Test
    void testFindByUser_whenUserHasToken_shouldReturnToken() {
        // Arrange
        User user = createTestUser();
        PasswordResetToken token = new PasswordResetToken(
                "test-user-token", 
                LocalDateTime.now().plusHours(1), 
                user);
        
        entityManager.persist(token);
        entityManager.flush();
        
        // Act
        Optional<PasswordResetToken> foundToken = passwordResetTokenRepository.findByUser(user);
        
        // Assert
        assertTrue(foundToken.isPresent());
        assertEquals(user, foundToken.get().getUser());
    }
    
    @Test
    void testDeleteByUser_shouldRemoveToken() {
        // Arrange
        User user = createTestUser();
        PasswordResetToken token = new PasswordResetToken(
                "test-delete-token", 
                LocalDateTime.now().plusHours(1), 
                user);
        
        entityManager.persist(token);
        entityManager.flush();
        
        // Act
        passwordResetTokenRepository.deleteByUser(user);
        entityManager.flush();
        Optional<PasswordResetToken> foundToken = passwordResetTokenRepository.findByUser(user);
        
        // Assert
        assertTrue(foundToken.isEmpty());
    }
    
    private User createTestUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPasswordHash("hashedPassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setActive(true);
        return userRepository.save(user);
    }
}