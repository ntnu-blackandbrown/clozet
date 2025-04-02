package stud.ntnu.no.backend.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.ntnu.no.backend.common.service.EmailService;

@RestController
@RequestMapping("/api/prod-test")
public class ProdMailTestController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/verification")
    public ResponseEntity<String> sendVerification() {
        String to = "kevindmazali@gmail.com";
        String token = "prod-token-" + System.currentTimeMillis();
        emailService.sendVerificationEmail(to, token);
        return ResponseEntity.ok("Verifikasjonsmail sendt til " + to);
    }
}
