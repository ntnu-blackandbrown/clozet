package stud.ntnu.no.backend.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class VerificationController {

    private final UserRepository userRepository;

    public VerificationController(UserRepository userRepository,
                                  EmailService emailService) {
        this.userRepository = userRepository;
        // emailService kan være valgfri her hvis du ikke sender e-post i selve verifiseringssteget
    }

    // Verifiser konto: GET /api/users/verify?token=xxxx
    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
        // Finn user basert på token
        Optional<User> optionalUser = userRepository.findAll().stream()
                .filter(u -> token.equals(u.getVerificationToken()))
                .findFirst();

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Ugyldig verifikasjonslenke");
        }
        User user = optionalUser.get();

        // Sjekk utløp
        if (user.getVerificationTokenExpiry() != null &&
            LocalDateTime.now().isAfter(user.getVerificationTokenExpiry())) {
            return ResponseEntity.badRequest().body("Verifikasjonslenken har utløpt.");
        }

        // Sett bruker aktiv
        user.setActive(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok("Bruker er verifisert! Du kan nå logge inn.");
    }
}
