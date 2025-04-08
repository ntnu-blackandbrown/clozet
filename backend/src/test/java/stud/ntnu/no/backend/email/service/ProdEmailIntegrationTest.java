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
public class ProdEmailIntegrationTest {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private EmailConfig emailConfig;

    @Test
    void testSendMailgunVerification() {

        String to = "clozet.adm.demo@gmail.com";
        String token = "prod-test-token";
        emailService.sendVerificationEmail(to, token);

        System.out.println("✅ Test verification email sent via Mailgun to " + to);
        System.out.println("🧪 Verify that the email has the link with token: " + token);

    }
}
