package stud.ntnu.no.backend.common.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.common.security.util.JwtUtils;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class VerificationController {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public VerificationController(UserRepository userRepository, JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token, HttpServletResponse response) {
        Optional<User> optionalUser = userRepository.findAll().stream()
            .filter(u -> token.equals(u.getVerificationToken()))
            .findFirst();

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Ugyldig verifikasjonslenke");
        }
        User user = optionalUser.get();

        if (user.getVerificationTokenExpiry() != null &&
            LocalDateTime.now().isAfter(user.getVerificationTokenExpiry())) {
            return ResponseEntity.badRequest().body("Verifikasjonslenken har utløpt.");
        }

        // Aktiver bruker
        user.setActive(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        // Auto-login: generer JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Sett cookie i responsen
        StringBuilder accessCookieBuilder = new StringBuilder();
        accessCookieBuilder.append("jwt=").append(jwt).append(";");
        accessCookieBuilder.append(" Max-Age=900;"); // 15 min
        accessCookieBuilder.append(" Path=/;");
        accessCookieBuilder.append(" HttpOnly;");
        accessCookieBuilder.append(" SameSite=Lax;");
        response.addHeader("Set-Cookie", accessCookieBuilder.toString());

        return ResponseEntity.ok("Bruker er verifisert og logget inn! Du kan nå få tilgang til dashboard.");
    }

}
