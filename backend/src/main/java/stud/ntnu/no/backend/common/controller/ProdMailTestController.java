package stud.ntnu.no.backend.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.common.service.EmailService;

/**
 * Controller for testing email sending in production.
 * <p>
 * Provides an endpoint to send a verification email.
 * </p>
 */
@RestController
@RequestMapping("/api/prod-test")
@Profile("prod") // Ensures this controller is active only in the production environment
public class ProdMailTestController {

    @Autowired
    private EmailService emailService;

    /**
     * Sends a verification email to a predefined address.
     *
     * @return a response entity indicating the email was sent
     */
    @GetMapping("/verification")
    public ResponseEntity<String> sendVerification() {
        String to = "kevindmazali@gmail.com";
        String token = "prod-token-" + System.currentTimeMillis();
        emailService.sendVerificationEmail(to, token);
        return ResponseEntity.ok("Verifikasjonsmail sendt til " + to);
    }
}
