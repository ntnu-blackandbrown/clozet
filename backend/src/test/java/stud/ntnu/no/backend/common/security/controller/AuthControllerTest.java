package stud.ntnu.no.backend.common.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {

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

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
        
        // Setup common mock behaviors
        when(userDetails.getUsername()).thenReturn("testuser");
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void registerUser_ShouldReturnSuccessMessage() throws Exception {
        // Given
        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setUsername("testuser");
        registerUserDTO.setEmail("test@example.com");
        registerUserDTO.setPassword("password123");
        
        doNothing().when(userService).createUserAndSendVerificationEmail(any(RegisterUserDTO.class));

        // When/Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("User registered successfully")));
                
        verify(userService).createUserAndSendVerificationEmail(any(RegisterUserDTO.class));
    }

    @Test
    void authenticateUser_WithValidUsername_ShouldReturnUserDetails() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("testuser");
        loginDTO.setPassword("password123");
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        // Set null fields explicitly to ensure they're documented
        userDTO.setId(null);
        userDTO.setFirstName(null);
        userDTO.setLastName(null);
        userDTO.setActive(false);
        userDTO.setRole(null);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(userService.getUserByUsername("testuser")).thenReturn(userDTO);

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andDo(document("auth-login",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("usernameOrEmail").description("Username or email for authentication"),
                                fieldWithPath("password").description("User password")
                        ),
                        responseFields(
                                fieldWithPath("username").description("Username of the authenticated user"),
                                fieldWithPath("email").description("Email of the authenticated user"),
                                fieldWithPath("id").description("User ID").optional(),
                                fieldWithPath("firstName").description("User's first name").optional(),
                                fieldWithPath("lastName").description("User's last name").optional(),
                                fieldWithPath("active").description("Whether the user account is active"),
                                fieldWithPath("role").description("User's role in the system").optional()
                        )
                ));
    }

    @Test
    void authenticateUser_WithValidEmail_ShouldReturnUserDetails() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("test@example.com");
        loginDTO.setPassword("password123");
        
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");
        
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh-token");
        when(userService.getUserByUsername("testuser")).thenReturn(userDTO);

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void authenticateUser_WithInvalidEmail_ShouldReturnUnauthorized() throws Exception {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("nonexistent@example.com");
        loginDTO.setPassword("password123");
        
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Invalid username/email or password")))
                .andDo(document("auth-login-error",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("usernameOrEmail").description("Non-existent username or email"),
                                fieldWithPath("password").description("User password")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("message").description("Error message for failed login")
                        )
                ));
    }

    @Test
    void verifyUser_WithValidToken_ShouldActivateUserAndReturnSuccess() throws Exception {
        // Given
        String token = "valid-token";
        
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");
        
        VerificationToken verificationToken = mock(VerificationToken.class);
        when(verificationToken.getUser()).thenReturn(user);
        when(verificationToken.isExpired()).thenReturn(false);
        
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(userDetails)).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("refresh-token");

        // When/Then
        mockMvc.perform(get("/api/auth/verify")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Email verified successfully")))
                .andDo(document("auth-verify",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        relaxedResponseFields(
                                fieldWithPath("message").description("Success message after verification")
                        )
                ));
                
        verify(user).setActive(true);
        verify(user).setUpdatedAt(any(LocalDateTime.class));
        verify(userRepository).save(user);
        verify(verificationTokenRepository).delete(verificationToken);
    }

    @Test
    void verifyUser_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Given
        String token = "invalid-token";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/auth/verify")
                .param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid verification token")))
                .andDo(document("auth-verify-invalid-token",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        relaxedResponseFields(
                                fieldWithPath("message").description("Error message for invalid token")
                        )
                ));
    }

    @Test
    void verifyUser_WithExpiredToken_ShouldReturnBadRequest() throws Exception {
        // Given
        String token = "expired-token";
        
        VerificationToken expiredToken = mock(VerificationToken.class);
        when(expiredToken.isExpired()).thenReturn(true);
        
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(expiredToken));

        // When/Then
        mockMvc.perform(get("/api/auth/verify")
                .param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Verification token has expired")));
    }

    @Test
    void logoutUser_ShouldReturnSuccessMessage() throws Exception {
        // When/Then
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Utlogging vellykket")))
                .andDo(document("auth-logout",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").description("Success message after logout")
                        )
                ));
    }

    @Test
    void requestPasswordReset_WithExistingEmail_ShouldSendEmail() throws Exception {
        // Given
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO();
        requestDTO.setEmail("test@example.com");
        
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@example.com");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.empty());
        when(emailConfig.getPasswordResetExpiryHours()).thenReturn(24);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString());

        // When/Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("If an account exists")))
                .andDo(document("auth-forgot-password",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("Email address to send password reset link")
                        ),
                        responseFields(
                                fieldWithPath("message").description("Generic message for security reasons")
                        )
                ));
                
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq("test@example.com"), anyString());
    }

    @Test
    void requestPasswordReset_WithNonExistingEmail_ShouldReturnGenericMessage() throws Exception {
        // Given
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO();
        requestDTO.setEmail("test@example.com");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("If an account exists")));
                
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetToken.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void validateResetToken_WithValidToken_ShouldReturnSuccess() throws Exception {
        // Given
        String token = "reset-token-123";
        
        PasswordResetToken resetToken = mock(PasswordResetToken.class);
        when(resetToken.isExpired()).thenReturn(false);
        
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        // When/Then
        mockMvc.perform(get("/api/auth/reset-password/validate")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Valid token")))
                .andDo(document("auth-validate-reset-token",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").description("Success message for valid token")
                        )
                ));
    }

    @Test
    void validateResetToken_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Given
        String token = "invalid-token";
        
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/auth/reset-password/validate")
                .param("token", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid or expired")));
    }

    @Test
    void resetPassword_WithValidToken_ShouldResetPasswordAndSendConfirmation() throws Exception {
        // Given
        PasswordResetDTO resetDTO = new PasswordResetDTO();
        resetDTO.setToken("reset-token-123");
        resetDTO.setPassword("newpassword123");
        
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@example.com");
        
        PasswordResetToken resetToken = mock(PasswordResetToken.class);
        when(resetToken.getUser()).thenReturn(user);
        when(resetToken.isExpired()).thenReturn(false);
        
        when(passwordResetTokenRepository.findByToken("reset-token-123")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("newpassword123")).thenReturn("newhashed");
        doNothing().when(emailService).sendPasswordResetConfirmationEmail(anyString());

        // When/Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Password has been reset successfully")))
                .andDo(document("auth-reset-password",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("token").description("Password reset token"),
                                fieldWithPath("password").description("New password")
                        ),
                        responseFields(
                                fieldWithPath("message").description("Success message after password reset")
                        )
                ));
                
        verify(user).setPasswordHash("newhashed");
        verify(user).setUpdatedAt(any(LocalDateTime.class));
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).delete(resetToken);
        verify(emailService).sendPasswordResetConfirmationEmail("test@example.com");
    }

    @Test
    void resetPassword_WithInvalidToken_ShouldReturnBadRequest() throws Exception {
        // Given
        PasswordResetDTO resetDTO = new PasswordResetDTO();
        resetDTO.setToken("reset-token-123");
        resetDTO.setPassword("newpassword123");
        
        when(passwordResetTokenRepository.findByToken("reset-token-123")).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid password reset token")));
    }

    @Test
    void resetPassword_WithExpiredToken_ShouldReturnBadRequest() throws Exception {
        // Given
        PasswordResetDTO resetDTO = new PasswordResetDTO();
        resetDTO.setToken("reset-token-123");
        resetDTO.setPassword("newpassword123");
        
        PasswordResetToken expiredToken = mock(PasswordResetToken.class);
        when(expiredToken.isExpired()).thenReturn(true);
        
        when(passwordResetTokenRepository.findByToken("reset-token-123")).thenReturn(Optional.of(expiredToken));

        // When/Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Password reset token has expired")))
                .andDo(document("auth-reset-password-expired",
                        Preprocessors.preprocessRequest(prettyPrint()),
                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("token").description("Expired password reset token"),
                                fieldWithPath("password").description("New password")
                        ),
                        responseFields(
                                fieldWithPath("message").description("Error message for expired token")
                        )
                ));
    }
}
