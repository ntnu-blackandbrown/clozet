package stud.ntnu.no.backend.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.common.security.util.JwtUtils;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.user.dto.LoginDTO;
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

    @Value("${app.jwt.cookie-max-age:900}")
    private int jwtCookieMaxAge;

    private final UserService userService;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, EmailService emailService,
                          JwtUtils jwtUtils, AuthenticationManager authenticationManager,
                          UserRepository userRepository, VerificationTokenRepository verificationTokenRepository,
                          PasswordResetTokenRepository passwordResetTokenRepository,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        logger.info("Registering new user: {}", registerUserDTO.getUsername());
        userService.createUserAndSendVerificationEmail(registerUserDTO);
        return ResponseEntity.ok("User registered successfully. Please check your email for verification.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        try {
            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            
            // Set authenticated user in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userDetails);
            
            // Create and set cookie
            setCookies(response, jwt);
            
            // Get user info
            UserDTO userDTO = userService.getUserByUsername(loginDTO.getUsername());
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token, HttpServletResponse response) {
        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByToken(token);
        
        if (verificationTokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification token");
        }
        
        VerificationToken verificationToken = verificationTokenOpt.get();
        
        if (verificationToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification token has expired");
        }
        
        User user = verificationToken.getUser();
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Delete the token
        verificationTokenRepository.delete(verificationToken);
        
        // Auto-login the user
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities("ROLE_USER")
                .build();
        
        String jwt = jwtUtils.generateJwtToken(userDetails);
        setCookies(response, jwt);
        
        return ResponseEntity.ok("Email verified successfully. You are now logged in.");
    }

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestParam("email") String email) {
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst();

        if (userOpt.isEmpty()) {
            // Don't reveal that the email doesn't exist
            return ResponseEntity.ok("If an account with that email exists, a password reset link has been sent.");
        }

        User user = userOpt.get();
        
        // Generate token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        
        // Save token
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        passwordResetTokenRepository.save(resetToken);
        
        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
        
        return ResponseEntity.ok("Password reset email sent successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                          @RequestParam("newPassword") String newPassword) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (LocalDateTime.now().isAfter(resetToken.getExpiryDate())) {
            passwordResetTokenRepository.delete(resetToken);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token has expired");
        }
        
        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Delete used token
        passwordResetTokenRepository.delete(resetToken);
        
        return ResponseEntity.ok("Password has been reset successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        // Clear the JWT cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setMaxAge(0);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);
        
        return ResponseEntity.ok("Logged out successfully");
    }

    private void setCookies(HttpServletResponse response, String jwt) {
        StringBuilder jwtCookieBuilder = new StringBuilder();
        jwtCookieBuilder.append("jwt=").append(jwt).append(";");
        jwtCookieBuilder.append(" Max-Age=").append(jwtCookieMaxAge).append(";");
        jwtCookieBuilder.append(" Path=/;");
        jwtCookieBuilder.append(" HttpOnly;");
        jwtCookieBuilder.append(" SameSite=Lax;");
        
        response.addHeader("Set-Cookie", jwtCookieBuilder.toString());
    }
} 