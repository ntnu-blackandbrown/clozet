package stud.ntnu.no.backend.common.security.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailConfig emailConfig;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthController authController;

    private RegisterUserDTO registerUserDTO;
    private LoginDTO loginDTO;
    private PasswordResetRequestDTO passwordResetRequestDTO;
    private PasswordResetDTO passwordResetDTO;
    private User user;
    private UserDTO userDTO;
    private VerificationToken verificationToken;
    private PasswordResetToken passwordResetToken;

    @BeforeEach
    void setUp() {
        // Initialize test data
        registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setUsername("testuser");
        registerUserDTO.setEmail("test@example.com");
        registerUserDTO.setPassword("password123");

        loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("testuser");
        loginDTO.setPassword("password123");

        passwordResetRequestDTO = new PasswordResetRequestDTO();
        passwordResetRequestDTO.setEmail("test@example.com");

        passwordResetDTO = new PasswordResetDTO();
        passwordResetDTO.setToken("reset-token-123");
        passwordResetDTO.setPassword("newpassword123");

        user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getEmail()).thenReturn("test@example.com");

        userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");

        verificationToken = mock(VerificationToken.class);
        when(verificationToken.getUser()).thenReturn(user);
        when(verificationToken.isExpired()).thenReturn(false);

        passwordResetToken = mock(PasswordResetToken.class);
        when(passwordResetToken.getUser()).thenReturn(user);
        when(passwordResetToken.isExpired()).thenReturn(false);

        // Setup only the common shared behavior
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
    }

    @Test
    void registerUser_ShouldReturnSuccessMessage() {
        // Given
        doNothing().when(userService).createUserAndSendVerificationEmail(any(RegisterUserDTO.class));

        // When
        ResponseEntity<?> response = authController.registerUser(registerUserDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(MessageResponse.class, response.getBody());
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertTrue(messageResponse.getMessage().contains("User registered successfully"));
        verify(userService).createUserAndSendVerificationEmail(registerUserDTO);
    }

    @Test
    void authenticateUser_WithValidUsername_ShouldReturnUserDetails() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(userService.getUserByUsername("testuser")).thenReturn(userDTO);

        // When
        ResponseEntity<?> responseEntity = authController.authenticateUser(loginDTO, response);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userDTO, responseEntity.getBody());
        verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void authenticateUser_WithValidEmail_ShouldReturnUserDetails() {
        // Given
        loginDTO.setUsernameOrEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(userService.getUserByUsername("testuser")).thenReturn(userDTO);

        // When
        ResponseEntity<?> responseEntity = authController.authenticateUser(loginDTO, response);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userDTO, responseEntity.getBody());
        verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void authenticateUser_WithInvalidEmail_ShouldReturnUnauthorized() {
        // Given
        loginDTO.setUsernameOrEmail("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = authController.authenticateUser(loginDTO, response);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Invalid username/email or password"));
    }

    @Test
    void verifyUser_WithValidToken_ShouldActivateUserAndReturnSuccess() {
        // Given
        String token = "valid-token";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh-token");

        // When
        ResponseEntity<?> responseEntity = authController.verifyUser(token, response);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Email verified successfully"));
        verify(user).setActive(true);
        verify(user).setUpdatedAt(any(LocalDateTime.class));
        verify(userRepository).save(user);
        verify(verificationTokenRepository).delete(verificationToken);
        verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void verifyUser_WithInvalidToken_ShouldReturnBadRequest() {
        // Given
        String token = "invalid-token";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = authController.verifyUser(token, response);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Invalid verification token"));
    }

    @Test
    void verifyUser_WithExpiredToken_ShouldReturnBadRequest() {
        // Given
        String token = "expired-token";
        VerificationToken expiredToken = mock(VerificationToken.class);
        when(expiredToken.isExpired()).thenReturn(true);
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(expiredToken));

        // When
        ResponseEntity<?> responseEntity = authController.verifyUser(token, response);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Verification token has expired"));
    }

    @Test
    void logoutUser_ShouldReturnSuccessMessage() {
        // When
        ResponseEntity<?> responseEntity = authController.logoutUser(response);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Utlogging vellykket"));
        verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void requestPasswordReset_WithExistingEmail_ShouldSendEmail() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.empty());
        when(emailConfig.getPasswordResetExpiryHours()).thenReturn(24);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // When
        ResponseEntity<?> responseEntity = authController.requestPasswordReset(passwordResetRequestDTO);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("If an account exists"));
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq("test@example.com"), anyString());
    }

    @Test
    void requestPasswordReset_WithNonExistingEmail_ShouldReturnGenericMessage() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = authController.requestPasswordReset(passwordResetRequestDTO);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("If an account exists"));
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void validateResetToken_WithValidToken_ShouldReturnSuccess() {
        // Given
        when(passwordResetTokenRepository.findByToken("reset-token-123")).thenReturn(Optional.of(passwordResetToken));

        // When
        ResponseEntity<?> responseEntity = authController.validateResetToken("reset-token-123");

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Valid token"));
    }

    @Test
    void validateResetToken_WithInvalidToken_ShouldReturnBadRequest() {
        // Given
        when(passwordResetTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = authController.validateResetToken("invalid-token");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Invalid or expired"));
    }

    @Test
    void resetPassword_WithValidToken_ShouldResetPasswordAndSendConfirmation() {
        // Given
        when(passwordResetTokenRepository.findByToken("reset-token-123")).thenReturn(Optional.of(passwordResetToken));
        when(passwordEncoder.encode("newpassword123")).thenReturn("newhashed");
        doNothing().when(emailService).sendPasswordResetConfirmationEmail(anyString());

        // When
        ResponseEntity<?> responseEntity = authController.resetPassword(passwordResetDTO);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Password has been reset successfully"));
        verify(user).setPasswordHash("newhashed");
        verify(user).setUpdatedAt(any(LocalDateTime.class));
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).delete(passwordResetToken);
        verify(emailService).sendPasswordResetConfirmationEmail("test@example.com");
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnBadRequest() {
        // Given
        when(passwordResetTokenRepository.findByToken("reset-token-123")).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = authController.resetPassword(passwordResetDTO);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Invalid password reset token"));
    }

    @Test
    void resetPassword_WithExpiredToken_ShouldReturnBadRequest() {
        // Given
        PasswordResetToken expiredToken = mock(PasswordResetToken.class);
        when(expiredToken.isExpired()).thenReturn(true);
        when(passwordResetTokenRepository.findByToken("reset-token-123")).thenReturn(Optional.of(expiredToken));

        // When
        ResponseEntity<?> responseEntity = authController.resetPassword(passwordResetDTO);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertTrue(((MessageResponse)responseEntity.getBody()).getMessage().contains("Password reset token has expired"));
    }
} 