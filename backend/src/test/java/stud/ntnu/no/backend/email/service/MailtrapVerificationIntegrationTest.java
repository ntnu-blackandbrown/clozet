package stud.ntnu.no.backend.email.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.common.config.EmailConfig;
import stud.ntnu.no.backend.common.service.EmailService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@ActiveProfiles("test") // Changed to test profile to align with other tests
@Tag("mailtrap")
@Disabled("Run manually when testing email integration")
public class MailtrapVerificationIntegrationTest {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private EmailConfig emailConfig;
    
    private boolean isMailtrapConfigured() {
        // Check if Mailtrap is configured properly
        return emailConfig != null && 
               emailConfig.getBaseUrl() != null && 
               emailConfig.getEmailFrom() != null &&
               System.getProperty("runMailtrapTests") != null;
    }

    @Test
    void testVerificationEmailDelivery() {
        // Skip test if Mailtrap is not configured
        assumeTrue(isMailtrapConfigured(), "Mailtrap is not configured properly or tests not explicitly enabled");
        
        // Arrange
        String testEmail = "test@example.com";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String token = "test-verification-token-" + timestamp;
        
        // Act - Send a real verification email to Mailtrap
        emailService.sendVerificationEmail(testEmail, token);
        
        // Assert - Manual verification
        String verificationLink = emailConfig.getBaseUrl() + "/verify?token=" + token;
        System.out.println("\n==== MAILTRAP VERIFICATION TEST ====");
        System.out.println("Verification email sent to Mailtrap inbox.");
        System.out.println("Email sent to: " + testEmail);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Verification link should contain: " + verificationLink);
        System.out.println("Please verify in Mailtrap inbox that:");
        System.out.println("1. Email was received");
        System.out.println("2. Subject is 'Bekreft din konto på Clozet'");
        System.out.println("3. Email contains the correct verification link");
        System.out.println("==========================================\n");
    }
}