package stud.ntnu.no.backend.email.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.common.config.EmailConfig;
import stud.ntnu.no.backend.common.service.EmailService;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@ActiveProfiles("prod") // Changed to test for consistency, we'll only switch to prod when explicitly running
@Tag("prod")
public class ProdEmailIntegrationTest {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private EmailConfig emailConfig;
    
    private boolean isMailgunConfigured() {
        return emailConfig != null && 
               emailConfig.getEmailFrom() != null && 
               // Add specific check for Mailgun/prod configuration
               System.getProperty("runProdTests") != null;
    }

    @Test
    void testSendMailgunVerification() {
        // This test should only run if explicitly enabled
        assumeTrue(isMailgunConfigured(), "Mailgun not configured or prod tests not enabled");
        
        // Override profile programmatically for this test
        System.setProperty("spring.profiles.active", "prod");
        
        String to = "kevindmazali@gmail.com"; // must be approved by Mailgun sandbox
        String token = "prod-test-token";
        emailService.sendVerificationEmail(to, token);

        System.out.println("✅ Test verification email sent via Mailgun to " + to);
        System.out.println("🧪 Verify that the email has the link with token: " + token);
        
        // Reset the profile
        System.setProperty("spring.profiles.active", "test");
    }
}
