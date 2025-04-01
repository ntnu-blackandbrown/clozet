package stud.ntnu.no.backend.common.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import stud.ntnu.no.backend.common.config.EmailConfig;
import stud.ntnu.no.backend.common.security.controller.AuthController;
import stud.ntnu.no.backend.common.security.util.JwtUtils;
import stud.ntnu.no.backend.common.service.EmailService;
import stud.ntnu.no.backend.user.dto.PasswordResetDTO;
import stud.ntnu.no.backend.user.dto.PasswordResetRequestDTO;
import stud.ntnu.no.backend.user.entity.PasswordResetToken;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.PasswordResetTokenRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;
import stud.ntnu.no.backend.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(AuthControllerPasswordResetTest.TestSecurityConfig.class)
class AuthControllerPasswordResetTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailConfig emailConfig;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("old-password-hash");
        testUser.setUsername("testuser");
        testUser.setActive(true);

        when(emailConfig.getPasswordResetExpiryHours()).thenReturn(1);
        
        // Reset mocks before each test
        reset(userRepository, passwordResetTokenRepository, emailService);
    }

    @Test
    void testForgotPassword_withExistingEmail_shouldSendResetEmail() throws Exception {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordResetTokenRepository.findByUser(testUser)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("If an account exists with that email, a password reset link has been sent."));
        
        // Verify token was created and email sent
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());
    }

    @Test
    void testForgotPassword_withNonExistingEmail_shouldReturnSameMessage() throws Exception {
        // Arrange
        String nonExistingEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + nonExistingEmail + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("If an account exists with that email, a password reset link has been sent."));
        
        // Verify no token was created or email sent
        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void testForgotPassword_withExistingToken_shouldDeleteAndCreateNewToken() throws Exception {
        // Arrange
        String email = "test@example.com";
        PasswordResetToken existingToken = new PasswordResetToken("old-token", LocalDateTime.now().plusHours(1), testUser);
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(passwordResetTokenRepository.findByUser(testUser)).thenReturn(Optional.of(existingToken));
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk());
        
        // Verify old token was deleted and new token created
        verify(passwordResetTokenRepository).delete(existingToken);
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(email), anyString());
    }
    
    @Test
    void testValidateResetToken_withValidToken_shouldReturnOk() throws Exception {
        // Arrange
        String token = "valid-token";
        PasswordResetToken resetToken = new PasswordResetToken(token, LocalDateTime.now().plusHours(1), testUser);
        
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        
        // Act & Assert
        mockMvc.perform(get("/api/auth/reset-password/validate")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Valid token"));
    }
    
    @Test
    void testValidateResetToken_withExpiredToken_shouldReturnBadRequest() throws Exception {
        // Arrange
        String token = "expired-token";
        PasswordResetToken resetToken = new PasswordResetToken(token, LocalDateTime.now().minusHours(1), testUser);
        
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        
        // Act & Assert
        mockMvc.perform(get("/api/auth/reset-password/validate")
                .param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid or expired password reset token"));
    }
    
    @Test
    void testResetPassword_withValidToken_shouldUpdatePasswordAndDeleteToken() throws Exception {
        // Arrange
        String token = "valid-token";
        String newPassword = "new-password";
        PasswordResetToken resetToken = new PasswordResetToken(token, LocalDateTime.now().plusHours(1), testUser);
        
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded-new-password");
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"" + token + "\", \"password\":\"" + newPassword + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password has been reset successfully"));
        
        // Verify password was updated and token deleted
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded-new-password", userCaptor.getValue().getPasswordHash());
        verify(passwordResetTokenRepository).delete(resetToken);
    }
    
    @Test
    void testResetPassword_withInvalidToken_shouldReturnBadRequest() throws Exception {
        // Arrange
        String token = "invalid-token";
        String newPassword = "new-password";
        
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        
        // Act & Assert
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"" + token + "\", \"password\":\"" + newPassword + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid password reset token"));
        
        // Verify nothing was changed
        verify(userRepository, never()).save(any());
    }

    // Security configuration for tests
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
        
        // Define all required mock beans
        @Bean
        public AuthenticationManager authenticationManager() {
            return mock(AuthenticationManager.class);
        }
        
        @Bean
        public JwtUtils jwtUtils() {
            return mock(JwtUtils.class);
        }
        
        @Bean
        public UserDetailsService userDetailsService() {
            return mock(UserDetailsService.class);
        }
        
        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }
        
        @Bean
        public EmailService emailService() {
            return mock(EmailService.class);
        }
        
        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }
        
        @Bean
        public VerificationTokenRepository verificationTokenRepository() {
            return mock(VerificationTokenRepository.class);
        }
        
        @Bean
        public PasswordResetTokenRepository passwordResetTokenRepository() {
            return mock(PasswordResetTokenRepository.class);
        }
        
        @Bean
        public PasswordEncoder passwordEncoder() {
            return mock(PasswordEncoder.class);
        }
        
        @Bean
        public EmailConfig emailConfig() {
            return mock(EmailConfig.class);
        }
    }

    private void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }
}