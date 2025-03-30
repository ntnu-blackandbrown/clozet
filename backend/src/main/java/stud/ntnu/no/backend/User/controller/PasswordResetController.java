package stud.ntnu.no.backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.user.entity.PasswordResetToken;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.PasswordResetTokenRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(UserRepository userRepository,
                                   PasswordResetTokenRepository resetTokenRepository,
                                   EmailService emailService,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // 1) Glemme passord
    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();

        if (userOpt.isEmpty()) {
            // Ikke avslør at e-posten ikke finnes
            return ResponseEntity.ok("Hvis denne e-postadressen finnes, sendes en reset-lenke...");
        }

        User user = userOpt.get();
        // Opprett token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken = new PasswordResetToken(token, expiry, user);
        resetTokenRepository.save(resetToken);

        // Send e-post med lenken
        String link = "http://localhost:8080/api/password/reset?token=" + token;
        String subject = "Tilbakestill passord";
        String message = "Hei!\nKlikk på denne lenken for å resette passord:\n" + link;

        emailService.sendTextEmail(user.getEmail(), subject, message);

        return ResponseEntity.ok("Hvis e-postadressen finnes, vil du få en tilbakestillingslenke.");
    }

    // 2) Resette passord
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestParam("newPassword") String newPassword) {
        Optional<PasswordResetToken> tokenOpt = resetTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Ugyldig eller utløpt token.");
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (LocalDateTime.now().isAfter(resetToken.getExpiryDate())) {
            return ResponseEntity.badRequest().body("Tilbakestillingslenken har utløpt.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Slett token for å unngå gjenbruk
        resetTokenRepository.delete(resetToken);

        return ResponseEntity.ok("Passordet er oppdatert.");
    }
}
