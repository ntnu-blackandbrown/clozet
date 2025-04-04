package stud.ntnu.no.backend.email.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.common.config.EmailConfig;
import stud.ntnu.no.backend.common.controller.MessageResponse;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.user.dto.PasswordResetDTO;
import stud.ntnu.no.backend.user.dto.PasswordResetRequestDTO;
import stud.ntnu.no.backend.user.entity.PasswordResetToken;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.PasswordResetTokenRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Changed to test profile to align with other tests
@Tag("mailtrap")
@Disabled("Run manually when testing email integration")
public class MailtrapPasswordResetIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private EmailConfig emailConfig;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private EmailService emailService;
    
    private boolean isMailtrapConfigured() {
        // Check if Mailtrap is configured properly
        return emailConfig != null && 
               emailConfig.getBaseUrl() != null && 
               emailConfig.getEmailFrom() != null &&
               System.getProperty("runMailtrapTests") != null;
    }

    @Test
    void testPasswordResetEmailDelivery() {
        // Skip test if Mailtrap is not configured
        assumeTrue(isMailtrapConfigured(), "Mailtrap is not configured properly or tests not explicitly enabled");
        
        // Arrange
        String testEmail = "test@example.com";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // Create or find test user
        User testUser;
        Optional<User> userOpt = userRepository.findByEmail(testEmail);
        if (userOpt.isEmpty()) {
            testUser = new User();
            testUser.setEmail(testEmail);
            testUser.setUsername("testuser");
            testUser.setPasswordHash("oldpassword");
            testUser.setActive(true);
            testUser = userRepository.save(testUser);
        } else {
            testUser = userOpt.get();
        }
        
        // Clean up any existing password reset tokens
        passwordResetTokenRepository.findByUser(testUser).ifPresent(passwordResetTokenRepository::delete);
        
        // Create password reset request
        PasswordResetRequestDTO resetRequest = new PasswordResetRequestDTO();
        resetRequest.setEmail(testEmail);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PasswordResetRequestDTO> request = new HttpEntity<>(resetRequest, headers);
        
        // Act - Send a real password reset request
        ResponseEntity<MessageResponse> response = restTemplate.postForEntity(
            "/api/auth/forgot-password", 
            request,
            MessageResponse.class
        );
        
        // Assert
        assertEquals(200, response.getStatusCodeValue());
        
        // Get the token for verification instructions
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByUser(testUser);
        assertTrue(tokenOpt.isPresent(), "Password reset token should be created");
        String token = tokenOpt.get().getToken();
        
        // Manual verification instructions
        System.out.println("\n==== MAILTRAP PASSWORD RESET TEST ====");
        System.out.println("Password reset email sent to Mailtrap inbox.");
        System.out.println("Email sent to: " + testEmail);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Token generated: " + token);
        System.out.println("Please verify in Mailtrap inbox that:");
        System.out.println("1. Email was received");
        System.out.println("2. Subject is 'Reset Your Password for Clozet'");
        System.out.println("3. Email contains a reset link with the token: " + token);
        System.out.println("4. Link format should be: " + emailConfig.getBaseUrl() + "/reset-password?token=" + token);
        System.out.println("==========================================\n");
    }

    @Test
    void testCompletePasswordResetFlow() {
        // Skip test if Mailtrap is not configured
        assumeTrue(isMailtrapConfigured(), "Mailtrap is not configured properly or tests not explicitly enabled");
        
        // Arrange
        String testEmail = "test@example.com";
        String oldPassword = "oldpassword";
        String newPassword = "newpassword" + System.currentTimeMillis();
        
        // Ensure test user exists
        User testUser;
        Optional<User> userOpt = userRepository.findByEmail(testEmail);
        if (userOpt.isEmpty()) {
            testUser = new User();
            testUser.setEmail(testEmail);
            testUser.setUsername("testuser");
            testUser.setPasswordHash(oldPassword);
            testUser.setActive(true);
            testUser = userRepository.save(testUser);
        } else {
            testUser = userOpt.get();
        }
        
        // Step 1: Request password reset
        PasswordResetRequestDTO resetRequest = new PasswordResetRequestDTO();
        resetRequest.setEmail(testEmail);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<PasswordResetRequestDTO> request = new HttpEntity<>(resetRequest, headers);
        ResponseEntity<MessageResponse> resetResponse = restTemplate.postForEntity(
            "/api/auth/forgot-password", 
            request,
            MessageResponse.class
        );
        
        assertEquals(200, resetResponse.getStatusCodeValue());
        
        // Step 2: Get token from repository
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByUser(testUser);
        assertTrue(tokenOpt.isPresent(), "Password reset token should be created");
        String token = tokenOpt.get().getToken();
        
        // Step 3: Validate token
        ResponseEntity<MessageResponse> validateResponse = restTemplate.getForEntity(
            "/api/auth/reset-password/validate?token=" + token,
            MessageResponse.class
        );
        
        assertEquals(200, validateResponse.getStatusCodeValue());
        
        // Step 4: Reset password
        PasswordResetDTO resetDTO = new PasswordResetDTO();
        resetDTO.setToken(token);
        resetDTO.setPassword(newPassword);
        
        HttpEntity<PasswordResetDTO> resetRequest2 = new HttpEntity<>(resetDTO, headers);
        ResponseEntity<MessageResponse> passwordChangeResponse = restTemplate.postForEntity(
            "/api/auth/reset-password",
            resetRequest2,
            MessageResponse.class
        );
        
        assertEquals(200, passwordChangeResponse.getStatusCodeValue());
        
        // Token should be deleted after successful reset
        assertFalse(passwordResetTokenRepository.findByToken(token).isPresent(), 
            "Token should be deleted after password reset");
        
        // Verify the user's password has changed
        testUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertNotEquals(oldPassword, testUser.getPasswordHash(), 
            "Password hash should have changed");
        
        System.out.println("\n==== PASSWORD RESET FLOW TEST ====");
        System.out.println("Complete password reset flow successful");
        System.out.println("User: " + testEmail);
        System.out.println("New password set to: " + newPassword);
        System.out.println("===================================\n");
    }
}