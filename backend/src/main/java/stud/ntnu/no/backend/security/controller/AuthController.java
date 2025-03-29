package stud.ntnu.no.backend.security.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.security.util.JwtUtils;
import stud.ntnu.no.backend.controller.MessageResponse;
import stud.ntnu.no.backend.user.dto.LoginDTO;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Value("${app.jwt.cookie-max-age}")
    private int jwtCookieMaxAge;

    @Value("${app.jwt.secure-cookie:false}")
    private boolean secureCookie;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginRequest, HttpServletResponse response) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        // Bygg cookie-strengen manuelt med SameSite=Lax
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append("jwt=").append(jwt).append(";");
        cookieBuilder.append(" Max-Age=").append(jwtCookieMaxAge).append(";");
        cookieBuilder.append(" Path=/;");
        cookieBuilder.append(" HttpOnly;");
        if (secureCookie) {
            cookieBuilder.append(" Secure;");
        }
        cookieBuilder.append(" SameSite=Lax");

        // Legg til headeren i responsen
        response.addHeader("Set-Cookie", cookieBuilder.toString());
        logger.info("Set-Cookie header: {}", cookieBuilder.toString());

        return ResponseEntity.ok(new MessageResponse("Innlogging vellykket"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        logger.info("Logout requested");

        // Invalidate cookie ved å sette maxAge=0
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append("jwt=;");
        cookieBuilder.append(" Max-Age=0;");
        cookieBuilder.append(" Path=/;");
        cookieBuilder.append(" HttpOnly;");
        if (secureCookie) {
            cookieBuilder.append(" Secure;");
        }
        cookieBuilder.append(" SameSite=Lax");

        response.addHeader("Set-Cookie", cookieBuilder.toString());
        logger.info("Set-Cookie header for logout: {}", cookieBuilder.toString());

        return ResponseEntity.ok(new MessageResponse("Utlogging vellykket"));
    }
}
