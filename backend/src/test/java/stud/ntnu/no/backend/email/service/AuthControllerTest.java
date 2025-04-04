package stud.ntnu.no.backend.email.service;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.common.controller.MessageResponse;
import stud.ntnu.no.backend.common.security.controller.AuthController;
import stud.ntnu.no.backend.common.security.util.JwtUtils;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.entity.VerificationToken;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;
import stud.ntnu.no.backend.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthController authController;

    // Disse blir vanligvis injisert via @Value i AuthController:
    private final int jwtCookieMaxAge = 900;
    private final int jwtRefreshCookieMaxAge = 604800;
    private final boolean secureCookie = false;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        org.springframework.test.util.ReflectionTestUtils.setField(authController, "jwtCookieMaxAge", jwtCookieMaxAge);
        org.springframework.test.util.ReflectionTestUtils.setField(authController, "jwtRefreshCookieMaxAge", jwtRefreshCookieMaxAge);
        org.springframework.test.util.ReflectionTestUtils.setField(authController, "secureCookie", secureCookie);
    }

    @Test
    void testVerifyEmailSuccess() throws Exception {
        // Arrange
        String token = "valid-token";
        User dummyUser = new User();
        dummyUser.setUsername("testuser");
        dummyUser.setPasswordHash("hashedpassword");
        dummyUser.setActive(false);

        // Opprett et gyldig VerificationToken med utløp i fremtiden
        VerificationToken verificationToken = new VerificationToken(token, LocalDateTime.now().plusHours(1), dummyUser);

        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        when(jwtUtils.generateJwtToken(any(UserDetails.class))).thenReturn("access-token");
        when(jwtUtils.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh-token");

        // Lag en dummy UserDetails for auto-login
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(dummyUser.getUsername())
                .password(dummyUser.getPasswordHash())
                .authorities("ROLE_USER")
                .build();
        when(userDetailsService.loadUserByUsername(dummyUser.getUsername())).thenReturn(userDetails);

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        ResponseEntity<?> result = authController.verifyEmail(token, response);

        // Assert
        // Verifiser at brukeren er aktivert
        verify(userRepository, times(1)).save(dummyUser);
        assertTrue(dummyUser.isActive(), "Brukeren bør være aktivert.");

        // Verifiser at token slettes
        verify(verificationTokenRepository, times(1)).delete(verificationToken);

        // Sjekk at JWT-cookies ble satt
        assertNotNull(response.getHeaders("Set-Cookie"));
        boolean foundAccess = response.getHeaders("Set-Cookie").stream().anyMatch(h -> h.contains("jwt=access-token"));
        boolean foundRefresh = response.getHeaders("Set-Cookie").stream().anyMatch(h -> h.contains("refreshToken=refresh-token"));
        assertTrue(foundAccess, "Access token cookie ble ikke satt.");
        assertTrue(foundRefresh, "Refresh token cookie ble ikke satt.");

        // Cast til MessageResponse og sjekk getMessage()
        MessageResponse messageResponse = (MessageResponse) result.getBody();
        assertNotNull(messageResponse, "Forventet at body er et MessageResponse-objekt.");

        // Sjekk at meldingen inneholder "Email verified successfully"
        assertTrue(
            messageResponse.getMessage().contains("Email verified successfully"),
            "Forventet suksessmelding i responsen."
        );
    }

    @Test
    void testVerifyEmailInvalidToken() {
        // Arrange
        String token = "invalid-token";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        ResponseEntity<?> result = authController.verifyEmail(token, response);

        // Assert
        assertEquals(400, result.getStatusCodeValue());

        // Cast til MessageResponse og sjekk getMessage()
        MessageResponse messageResponse = (MessageResponse) result.getBody();
        assertNotNull(messageResponse, "Forventet at body er et MessageResponse-objekt.");

        assertTrue(
            messageResponse.getMessage().contains("Invalid verification token"),
            "Forventet feilmelding i responsen."
        );
    }

    @Test
    void testVerifyEmailExpiredToken() {
        // Arrange
        String token = "expired-token";
        User dummyUser = new User();
        dummyUser.setUsername("testuser");
        dummyUser.setPasswordHash("hashedpassword");
        dummyUser.setActive(false);

        // Opprett et VerificationToken med utløpt tid
        VerificationToken verificationToken = new VerificationToken(token, LocalDateTime.now().minusHours(1), dummyUser);
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        ResponseEntity<?> result = authController.verifyEmail(token, response);

        // Assert
        assertEquals(400, result.getStatusCodeValue());

        // Cast til MessageResponse og sjekk getMessage()
        MessageResponse messageResponse = (MessageResponse) result.getBody();
        assertNotNull(messageResponse, "Forventet at body er et MessageResponse-objekt.");

        assertTrue(
            messageResponse.getMessage().contains("Verification token has expired"),
            "Forventet feilmelding om utløpt token i responsen."
        );
    }
}
