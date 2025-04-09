package stud.ntnu.no.backend.common.security.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

/**
 * REST controller for handling authentication-related requests.
 *
 * <p>This controller provides endpoints for user registration, login, email verification, password
 * reset, and logout.
 */
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
  private int jwtCookieMaxAge;

  @Value("${app.jwt.refresh-cookie-max-age}")
  private int jwtRefreshCookieMaxAge;

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
   * Registers a new user and sends a verification email.
   *
   * @param registerUserDTO the user registration data
   * @return a response entity with a success message
   */
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
    logger.info("Registering new user: {}", registerUserDTO.getUsername());
    userService.createUserAndSendVerificationEmail(registerUserDTO);
    return ResponseEntity.ok(
        new MessageResponse(
            "User registered successfully. Please check your email for verification."));
  }

  /**
   * Authenticates a user and sets JWT cookies.
   *
   * @param loginRequest the login request data
   * @param response the HTTP response
   * @return a response entity with user details or an error message
   */
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(
      @RequestBody LoginDTO loginRequest, HttpServletResponse response) {
    logger.info("Login attempt with: {}", loginRequest.getUsernameOrEmail());

    // Bestem om input er e-post eller brukernavn
    String usernameOrEmail = loginRequest.getUsernameOrEmail();
    String username = usernameOrEmail;

    // Hvis det ser ut som en e-postadresse, hent tilsvarende brukernavn
    if (usernameOrEmail.contains("@")) {
      Optional<User> userOpt = userRepository.findByEmail(usernameOrEmail);
      if (userOpt.isPresent()) {
        username = userOpt.get().getUsername();
      } else {
        // Ikke avslør om e-posten eksisterer for sikkerhet
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new MessageResponse("Invalid username/email or password"));
      }
    }

    try {
      // Autentiser med brukernavn
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));

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
      logger.info("Set-Cookie header (access token): {}", accessCookieBuilder);

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
      logger.info("Set-Cookie header (refresh token): {}", refreshCookieBuilder);

      // Hent brukerinfo og returner
      try {
        UserDTO userDTO = userService.getUserByUsername(username);
        return ResponseEntity.ok(userDTO);
      } catch (Exception e) {
        logger.warn("Could not fetch user details, but login was successful", e);
        return ResponseEntity.ok(new MessageResponse("Innlogging vellykket"));
      }
    } catch (Exception e) {
      logger.error("Authentication failed: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new MessageResponse("Invalid username/email or password"));
    }
  }

  /**
   * Verifies a user's email using a token.
   *
   * @param token the verification token
   * @param response the HTTP response
   * @return a response entity with a success or error message
   */
  @GetMapping("/verify")
  public ResponseEntity<?> verifyUser(@RequestParam String token, HttpServletResponse response) {
    Optional<VerificationToken> verificationTokenOpt =
        verificationTokenRepository.findByToken(token);

    if (verificationTokenOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponse("Invalid verification token"));
    }

    VerificationToken verificationToken = verificationTokenOpt.get();

    if (verificationToken.isExpired()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new MessageResponse("Verification token has expired"));
    }

    User user = verificationToken.getUser();
    user.setActive(true);
    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);

    // Slett tokenen
    verificationTokenRepository.delete(verificationToken);

    // Auto-logg inn brukeren
    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

    // Generer access-token og refresh-token
    String accessToken = jwtUtils.generateJwtToken(userDetails);
    String refreshToken = jwtUtils.generateRefreshToken(userDetails);

    // Sett cookies
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

    return ResponseEntity.ok(
        new MessageResponse("Email verified successfully. You are now logged in."));
  }

  /**
   * Logs out the user by invalidating tokens.
   *
   * @param response the HTTP response
   * @return a response entity with a success message
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
    logger.info("Set-Cookie header for logout (access token): {}", accessCookieBuilder);

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
    logger.info("Set-Cookie header for logout (refresh token): {}", refreshCookieBuilder);

    return ResponseEntity.ok(new MessageResponse("Utlogging vellykket"));
  }

  /**
   * Requests a password reset email.
   *
   * @param request the password reset request data
   * @return a response entity with a success message
   */
  @PostMapping("/forgot-password")
  public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequestDTO request) {
    logger.info("Password reset requested for email: {}", request.getEmail());

    Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
    if (userOpt.isEmpty()) {
      // Ikke avslør om e-posten finnes for sikkerhet
      return ResponseEntity.ok(
          new MessageResponse(
              "If an account exists with that email, a password reset link has been sent."));
    }

    User user = userOpt.get();

    // Slett eventuelle eksisterende tokens for denne brukeren
    passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

    // Generer en ny token
    String token = UUID.randomUUID().toString();
    int expiryHours = emailConfig.getPasswordResetExpiryHours();
    PasswordResetToken passwordResetToken =
        new PasswordResetToken(token, LocalDateTime.now().plusHours(expiryHours), user);

    passwordResetTokenRepository.save(passwordResetToken);

    // Send e-post med reset-link
    emailService.sendPasswordResetEmail(user.getEmail(), token);

    return ResponseEntity.ok(
        new MessageResponse(
            "If an account exists with that email, a password reset link has been sent."));
  }

  /**
   * Validates a password reset token.
   *
   * @param token the reset token
   * @return a response entity with a success or error message
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
   * Resets the password using a token.
   *
   * @param resetRequest the password reset request data
   * @return a response entity with a success message
   */
  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO resetRequest) {
    Optional<PasswordResetToken> tokenOpt =
        passwordResetTokenRepository.findByToken(resetRequest.getToken());

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

    // Slett tokenen etter vellykket reset
    passwordResetTokenRepository.delete(resetToken);

    // Send bekreftelsesepost
    emailService.sendPasswordResetConfirmationEmail(user.getEmail());

    return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
  }

  /**
   * Extracts the value of a cookie by name.
   *
   * @param request the HTTP request
   * @param cookieName the name of the cookie
   * @return the cookie value or null if not found
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
