package stud.ntnu.no.backend.common.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
public class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @InjectMocks
    private AuthController authController;

    // Testdata
    private RegisterUserDTO registerUserDTO;
    private LoginDTO loginDTO;
    private PasswordResetRequestDTO passwordResetRequestDTO;
    private PasswordResetDTO passwordResetDTO;
    private User user;
    private UserDTO userDTO;
    private VerificationToken verificationToken;
    private PasswordResetToken passwordResetToken;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        // Bygg MockMvc med standalone-setup og REST Docs-konfigurasjon
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .apply(documentationConfiguration(restDocumentation))
            .build();

        // Opprett testdata
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
    }

    @Test
    void registerUser_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(userService).createUserAndSendVerificationEmail(any(RegisterUserDTO.class));

        // Act
        ResultActions result = mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerUserDTO)));

        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("User registered successfully")))
            .andDo(document("register-user",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));

        verify(userService).createUserAndSendVerificationEmail(registerUserDTO);
    }

    @Test
    void authenticateUser_WithValidUsername_ShouldReturnUserDetails() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(userService.getUserByUsername("testuser")).thenReturn(userDTO);

        // Act
        ResultActions result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)));

        // Assert
        result.andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(userDTO)))
            .andDo(document("authenticate-user",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));
    }

    @Test
    void authenticateUser_WithInvalidEmail_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        loginDTO.setUsernameOrEmail("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        ResultActions result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)));

        // Assert
        result.andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message", containsString("Invalid username/email or password")))
            .andDo(document("authenticate-user-invalid",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));
    }

    @Test
    void verifyUser_WithValidToken_ShouldActivateUserAndReturnSuccess() throws Exception {
        // Arrange
        String token = "valid-token";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh-token");

        // Act
        ResultActions result = mockMvc.perform(get("/api/auth/verify")
            .param("token", token)
            .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("Email verified successfully")))
            .andDo(document("verify-user",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));

        verify(user, times(1)).setActive(true);
        verify(user, times(1)).setUpdatedAt(any(LocalDateTime.class));
        verify(userRepository, times(1)).save(user);
        verify(verificationTokenRepository, times(1)).delete(verificationToken);
    }

    @Test
    void verifyUser_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String token = "invalid-token";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Act
        ResultActions result = mockMvc.perform(get("/api/auth/verify")
            .param("token", token)
            .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Invalid verification token")))
            .andDo(document("verify-user-invalid",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));
    }

    @Test
    void verifyUser_WithExpiredToken_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String token = "expired-token";
        VerificationToken expiredToken = mock(VerificationToken.class);
        when(expiredToken.isExpired()).thenReturn(true);
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(expiredToken));

        // Act
        ResultActions result = mockMvc.perform(get("/api/auth/verify")
            .param("token", token)
            .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Verification token has expired")))
            .andDo(document("verify-user-expired",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));
    }

    @Test
    void logoutUser_ShouldReturnSuccessMessage() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/api/auth/logout")
            .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("Utlogging vellykket")))
            .andDo(document("logout-user",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));
    }

    @Test
    void requestPasswordReset_WithExistingEmail_ShouldSendEmail() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.empty());
        when(emailConfig.getPasswordResetExpiryHours()).thenReturn(24);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // Act
        ResultActions result = mockMvc.perform(post("/api/auth/request-password-reset")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passwordResetRequestDTO)));

        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("If an account exists")))
            .andDo(document("request-password-reset",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));

        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq("test@example.com"), anyString());
    }

    @Test
    void requestPasswordReset_WithNonExistingEmail_ShouldReturnGenericMessage() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act
        ResultActions result = mockMvc.perform(post("/api/auth/request-password-reset")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passwordResetRequestDTO)));

        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("If an account exists")))
            .andDo(document("request-password-reset-nonexistent",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));

        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void validateResetToken_WithValidToken_ShouldReturnSuccess() throws Exception {
        // Arrange
        when(passwordResetTokenRepository.findByToken("reset-token-123"))
            .thenReturn(Optional.of(passwordResetToken));

        // Act
        ResultActions result = mockMvc.perform(get("/api/auth/validate-reset-token")
            .param("token", "reset-token-123")
            .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("Valid token")))
            .andDo(document("validate-reset-token",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));
    }

    @Test
    void validateResetToken_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(passwordResetTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        ResultActions result = mockMvc.perform(get("/api/auth/validate-reset-token")
            .param("token", "invalid-token")
            .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Invalid or expired")))
            .andDo(document("validate-reset-token-invalid",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));
    }

    @Test
    void resetPassword_WithValidToken_ShouldResetPasswordAndSendConfirmation() throws Exception {
        // Arrange
        when(passwordResetTokenRepository.findByToken("reset-token-123"))
            .thenReturn(Optional.of(passwordResetToken));
        when(passwordEncoder.encode("newpassword123")).thenReturn("newhashed");
        doNothing().when(emailService).sendPasswordResetConfirmationEmail(anyString());

        // Act
        ResultActions result = mockMvc.perform(post("/api/auth/reset-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passwordResetDTO)));

        // Assert
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("Password has been reset successfully")))
            .andDo(document("reset-password",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));

        verify(user, times(1)).setPasswordHash("newhashed");
        verify(user, times(1)).setUpdatedAt(any(LocalDateTime.class));
        verify(userRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).delete(passwordResetToken);
        verify(emailService, times(1)).sendPasswordResetConfirmationEmail("test@example.com");
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(passwordResetTokenRepository.findByToken("reset-token-123"))
            .thenReturn(Optional.empty());

        // Act
        ResultActions result = mockMvc.perform(post("/api/auth/reset-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passwordResetDTO)));

        // Assert
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Invalid password reset token")))
            .andDo(document("reset-password-invalid",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));
    }

    @Test
    void resetPassword_WithExpiredToken_ShouldReturnBadRequest() throws Exception {
        // Arrange
        PasswordResetToken expiredToken = mock(PasswordResetToken.class);
        when(expiredToken.isExpired()).thenReturn(true);
        when(passwordResetTokenRepository.findByToken("reset-token-123"))
            .thenReturn(Optional.of(expiredToken));

        // Act
        ResultActions result = mockMvc.perform(post("/api/auth/reset-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passwordResetDTO)));

        // Assert
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Password reset token has expired")))
            .andDo(document("reset-password-expired",
                Preprocessors.preprocessRequest(prettyPrint()),
                Preprocessors.preprocessResponse(prettyPrint())
            ));
    }
}
