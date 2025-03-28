package stud.ntnu.no.backend.controller;

import jakarta.servlet.http.Cookie;
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
import stud.ntnu.no.backend.common.util.JwtUtils;
import stud.ntnu.no.backend.user.dto.LoginDTO;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Value("${app.jwt.cookie-max-age}")
    private int jwtCookieMaxAge;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginRequest,
                                            HttpServletResponse response) {
        logger.info("Login-forsøk for bruker: {}", loginRequest.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        String jwt = jwtUtils.generateJwtToken(userDetails);
        
        // Opprett en sikker cookie
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Bruk kun for HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(jwtCookieMaxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
        
        logger.info("Bruker {} logget på vellykket", loginRequest.getUsername());
        return ResponseEntity.ok(new MessageResponse("Innlogging vellykket"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        logger.info("Utlogging forespurt");
        
        // Ugyldiggjør cookies
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        logger.info("Bruker logget ut vellykket");
        return ResponseEntity.ok(new MessageResponse("Utlogging vellykket"));
    }
}