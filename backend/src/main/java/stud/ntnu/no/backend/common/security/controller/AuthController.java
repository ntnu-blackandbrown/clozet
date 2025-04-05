package stud.ntnu.no.backend.common.security.controller;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.common.config.EmailConfig;
import stud.ntnu.no.backend.common.controller.MessageResponse;
import stud.ntnu.no.backend.common.security.util.JwtUtils;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.user.dto.LoginDTO;
import stud.ntnu.no.backend.user.dto.PasswordResetDTO;
import stud.ntnu.no.backend.user.dto.PasswordResetRequestDTO;
import stud.ntnu.no.backend.user.dto.RegisterUserDTO;
import stud.ntnu.no.backend.user.dto.UserDTO;
import stud.ntnu.no.backend.user.entity.PasswordResetToken;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.entity.VerificationToken;
import stud.ntnu.no.backend.user.repository.PasswordResetTokenRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;
import stud.ntnu.no.backend.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailConfig emailConfig;

    @Value("${app.jwt.cookie-max-age}")
    private int jwtCookieMaxAge;  // For access-token (f.eks. 900 sekunder = 15 minutter)

    @Value("${app.jwt.refresh-cookie-max-age}")
    private int jwtRefreshCookieMaxAge;  // For refresh-token (f.eks. 604800 sekunder = 7 dager)

    @Value("${app.jwt.secure-cookie:false}")
    private boolean secureCookie;

    public AuthController(
            AuthenticationManager authenticationManager, 
            JwtUtils jwtUtils, 
            UserDetailsService userDetailsService,
            UserService userService,
            EmailService emailService,
            UserRepository userRepository,
            VerificationTokenRepository verificationTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailConfig emailConfig,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailConfig = emailConfig;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user and send verification email
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        logger.info("Registering new user: {}", registerUserDTO.getUsername());
        userService.createUserAndSendVerificationEmail(registerUserDTO);
        return ResponseEntity.ok(new MessageResponse("User registered successfully. Please check your email for verification."));
    }

    /**
     * Authenticate a user and set JWT cookies
     */
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

        // Generate access-token and refresh-token
        String accessToken = jwtUtils.generateJwtToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        // Set access-token cookie
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

        // Set refresh-token cookie
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

        // Get user info and return it
        try {
            UserDTO userDTO = userService.getUserByUsername(loginRequest.getUsername());
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.warn("Could not fetch user details, but login was successful", e);
            return ResponseEntity.ok(new MessageResponse("Innlogging vellykket"));
        }
    }

    /**
     * Verify a user's email using token
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token, HttpServletResponse response) {
        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByToken(token);
        
        if (verificationTokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Invalid verification token"));
        }
        
        VerificationToken verificationToken = verificationTokenOpt.get();
        
        if (verificationToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Verification token has expired"));
        }
        
        User user = verificationToken.getUser();
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Delete the token
        verificationTokenRepository.delete(verificationToken);
        
        // Auto-login the user
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        
        // Generate access-token and refresh-token
        String accessToken = jwtUtils.generateJwtToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        
        // Set cookies
        // Set access-token cookie
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
        
        // Set refresh-token cookie
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
        
        return ResponseEntity.ok(new MessageResponse("Email verified successfully. You are now logged in."));
    }

    /**
     * Logout user by invalidating tokens
     */
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

    /**
     * Refresh an expired access token using refresh token
     */
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

    /**
     * Request password reset email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequestDTO request) {
        logger.info("Password reset requested for email: {}", request.getEmail());

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            // Don't reveal if email exists or not for security
            return ResponseEntity.ok(new MessageResponse(
                "If an account exists with that email, a password reset link has been sent."));
        }

        User user = userOpt.get();

        // Delete any existing token for this user
        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

        // Generate a new token
        String token = UUID.randomUUID().toString();
        int expiryHours = emailConfig.getPasswordResetExpiryHours();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
            token,
            LocalDateTime.now().plusHours(expiryHours),
            user);

        passwordResetTokenRepository.save(passwordResetToken);

        // Send email with reset link
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return ResponseEntity.ok(new MessageResponse(
            "If an account exists with that email, a password reset link has been sent."));
    }

    /**
     * Validate reset token
     */
    @GetMapping("/reset-password/validate")
    public ResponseEntity<?> validateResetToken(@RequestParam("token") String token) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("Invalid or expired password reset token"));
        }

        return ResponseEntity.ok(new MessageResponse("Valid token"));
    }

    /**
     * Reset password with token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO resetRequest) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(resetRequest.getToken());

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("Invalid password reset token"));
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("Password reset token has expired"));
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(resetRequest.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Delete the token
        passwordResetTokenRepository.delete(resetToken);

        // Send confirmation email
        emailService.sendPasswordResetConfirmationEmail(user.getEmail());

        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
    }

    /**
     * Helper method to extract cookie value
     */
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