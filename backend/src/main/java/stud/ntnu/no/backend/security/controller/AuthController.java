package stud.ntnu.no.backend.security.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.controller.MessageResponse;
import stud.ntnu.no.backend.security.util.JwtUtils;
import stud.ntnu.no.backend.user.dto.LoginDTO;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Value("${app.jwt.cookie-max-age}")
    private int jwtCookieMaxAge;  // For access-token (f.eks. 900 sekunder = 15 minutter)

    @Value("${app.jwt.refresh-cookie-max-age}")
    private int jwtRefreshCookieMaxAge;  // For refresh-token (f.eks. 604800 sekunder = 7 dager)

    @Value("${app.jwt.secure-cookie:false}")
    private boolean secureCookie;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
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

        // Generer access-token og refresh-token
        String accessToken = jwtUtils.generateJwtToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        // Sett access-token cookie
        StringBuilder accessCookieBuilder = new StringBuilder();
        accessCookieBuilder.append("jwt=").append(accessToken).append(";");
        accessCookieBuilder.append(" Max-Age=").append(jwtCookieMaxAge).append(";");
        accessCookieBuilder.append(" Path=/;");
        accessCookieBuilder.append(" HttpOnly;");
        if (secureCookie) {
            accessCookieBuilder.append(" Secure;");
        }
        accessCookieBuilder.append(" SameSite=Lax");
        response.addHeader("Set-Cookie", accessCookieBuilder.toString());
        logger.info("Set-Cookie header (access token): {}", accessCookieBuilder.toString());

        // Sett refresh-token cookie
        StringBuilder refreshCookieBuilder = new StringBuilder();
        refreshCookieBuilder.append("refreshToken=").append(refreshToken).append(";");
        refreshCookieBuilder.append(" Max-Age=").append(jwtRefreshCookieMaxAge).append(";");
        refreshCookieBuilder.append(" Path=/;");
        refreshCookieBuilder.append(" HttpOnly;");
        if (secureCookie) {
            refreshCookieBuilder.append(" Secure;");
        }
        refreshCookieBuilder.append(" SameSite=Lax");
        response.addHeader("Set-Cookie", refreshCookieBuilder.toString());
        logger.info("Set-Cookie header (refresh token): {}", refreshCookieBuilder.toString());

        return ResponseEntity.ok(new MessageResponse("Innlogging vellykket"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        logger.info("Logout requested");

        // Invalider access-token cookie
        StringBuilder accessCookieBuilder = new StringBuilder();
        accessCookieBuilder.append("jwt=;");
        accessCookieBuilder.append(" Max-Age=0;");
        accessCookieBuilder.append(" Path=/;");
        accessCookieBuilder.append(" HttpOnly;");
        if (secureCookie) {
            accessCookieBuilder.append(" Secure;");
        }
        accessCookieBuilder.append(" SameSite=Lax");
        response.addHeader("Set-Cookie", accessCookieBuilder.toString());
        logger.info("Set-Cookie header for logout (access token): {}", accessCookieBuilder.toString());

        // Invalider refresh-token cookie
        StringBuilder refreshCookieBuilder = new StringBuilder();
        refreshCookieBuilder.append("refreshToken=;");
        refreshCookieBuilder.append(" Max-Age=0;");
        refreshCookieBuilder.append(" Path=/;");
        refreshCookieBuilder.append(" HttpOnly;");
        if (secureCookie) {
            refreshCookieBuilder.append(" Secure;");
        }
        refreshCookieBuilder.append(" SameSite=Lax");
        response.addHeader("Set-Cookie", refreshCookieBuilder.toString());
        logger.info("Set-Cookie header for logout (refresh token): {}", refreshCookieBuilder.toString());

        return ResponseEntity.ok(new MessageResponse("Utlogging vellykket"));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Refresh token request");

        // Hent refresh-token fra cookie
        String refreshToken = getCookieValue(request, "refreshToken");
        if (refreshToken != null && jwtUtils.validateJwtToken(refreshToken)) { // Du kan eventuelt lage en egen valideringsmetode for refresh-token
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            logger.info("Refresh token valid for user: {}", username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtUtils.generateJwtToken(userDetails);

            // Sett ny access-token cookie
            StringBuilder accessCookieBuilder = new StringBuilder();
            accessCookieBuilder.append("jwt=").append(newAccessToken).append(";");
            accessCookieBuilder.append(" Max-Age=").append(jwtCookieMaxAge).append(";");
            accessCookieBuilder.append(" Path=/;");
            accessCookieBuilder.append(" HttpOnly;");
            if (secureCookie) {
                accessCookieBuilder.append(" Secure;");
            }
            accessCookieBuilder.append(" SameSite=Lax");
            response.addHeader("Set-Cookie", accessCookieBuilder.toString());
            logger.info("Set-Cookie header (new access token): {}", accessCookieBuilder.toString());

            return ResponseEntity.ok(new MessageResponse("Token refreshed successfully"));
        } else {
            logger.warn("Invalid or missing refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Refresh token invalid or missing"));
        }
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
