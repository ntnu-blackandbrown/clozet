package stud.ntnu.no.backend.common.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private UserDetails userDetails;
    // Generate a proper secure key for HS512 algorithm
    private final String jwtSecret = Base64.getEncoder().encodeToString(
            Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
    private final int jwtExpirationMs = 60000; // 1 minute
    private final int jwtRefreshExpirationMs = 300000; // 5 minutes
    private final String testUsername = "testuser";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
        ReflectionTestUtils.setField(jwtUtils, "jwtRefreshExpirationMs", jwtRefreshExpirationMs);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testUsername);
    }

    @Test
    void generateJwtToken_ShouldCreateValidToken() {
        String token = jwtUtils.generateJwtToken(userDetails);
        
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals(testUsername, jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        String token = jwtUtils.generateRefreshToken(userDetails);
        
        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals(testUsername, jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void validateJwtToken_WithValidToken_ShouldReturnTrue() {
        String token = jwtUtils.generateJwtToken(userDetails);
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_WithInvalidToken_ShouldReturnFalse() {
        assertFalse(jwtUtils.validateJwtToken("invalidToken"));
    }

    @Test
    void getUsernameFromToken_WithValidToken_ShouldReturnUsername() {
        String token = jwtUtils.generateJwtToken(userDetails);
        assertEquals(testUsername, jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void tokenExpiry_ShouldReflectConfiguredExpiration() throws Exception {
        // Access private method using reflection to get all claims
        java.lang.reflect.Method getAllClaimsMethod = JwtUtils.class.getDeclaredMethod("getAllClaimsFromToken", String.class);
        getAllClaimsMethod.setAccessible(true);
        
        // Test access token expiry
        String accessToken = jwtUtils.generateJwtToken(userDetails);
        Claims accessClaims = (Claims) getAllClaimsMethod.invoke(jwtUtils, accessToken);
        Date accessExpiry = accessClaims.getExpiration();
        Date now = new Date();
        
        long diffInMs = accessExpiry.getTime() - now.getTime();
        // Allow for a small buffer in the test (token generation takes some time)
        assertTrue(diffInMs <= jwtExpirationMs && diffInMs > jwtExpirationMs - 1000);
        
        // Test refresh token expiry
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        Claims refreshClaims = (Claims) getAllClaimsMethod.invoke(jwtUtils, refreshToken);
        Date refreshExpiry = refreshClaims.getExpiration();
        
        diffInMs = refreshExpiry.getTime() - now.getTime();
        assertTrue(diffInMs <= jwtRefreshExpirationMs && diffInMs > jwtRefreshExpirationMs - 1000);
    }
} 