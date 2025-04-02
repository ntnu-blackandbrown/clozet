package stud.ntnu.no.backend.email.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.common.service.EmailService;

@SpringBootTest
@ActiveProfiles("prod") // Bruker Mailgun!
public class ProdEmailIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendMailgunVerification() {
        String to = "kevindmazali@gmail.com"; // må være godkjent av Mailgun sandbox
        String token = "prod-test-token";
        emailService.sendVerificationEmail(to, token);

        System.out.println("✅ Test-verifikasjonsmail sendt via Mailgun til " + to);
        System.out.println("🧪 Verifiser at e-posten har lenken med token: " + token);
    }
}
