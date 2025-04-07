package stud.ntnu.no.backend.common.security.config;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import stud.ntnu.no.backend.common.security.filter.JwtAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthFilter;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private HttpServletRequest request;
    
    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder.getClass().getName().contains("BCrypt"));
        
        // Test encoding and matching with BCrypt
        String password = "testPassword";
        String encoded = passwordEncoder.encode(password);
        
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$"));
        assertTrue(passwordEncoder.matches(password, encoded));
        assertFalse(passwordEncoder.matches("wrongPassword", encoded));
    }

    @Test
    void authenticationManager_ShouldReturnManagerFromConfiguration() throws Exception {
        // Given
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(expectedManager);
        
        // When
        AuthenticationManager actualManager = securityConfig.authenticationManager(authenticationConfiguration);
        
        // Then
        assertSame(expectedManager, actualManager);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void corsConfigurationSource_ShouldConfigureCorsCorrectly() {
        // Given
        String localOrigin = "http://localhost:5173";
        String prodOrigin = "https://clozet.netlify.app";
        
        // When
        CorsConfigurationSource corsConfigSource = securityConfig.corsConfigurationSource();
        
        // Then
        assertNotNull(corsConfigSource);
        
        // Test with localhost origin
        when(request.getHeader("Origin")).thenReturn(localOrigin);
        CorsConfiguration configForLocal = corsConfigSource.getCorsConfiguration(request);
        
        assertNotNull(configForLocal);
        assertTrue(configForLocal.getAllowedOrigins().contains(localOrigin));
        assertTrue(configForLocal.getAllowedMethods().containsAll(
                java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")));
        assertTrue(configForLocal.getAllowCredentials());
        
        // Test with production origin
        when(request.getHeader("Origin")).thenReturn(prodOrigin);
        CorsConfiguration configForProd = corsConfigSource.getCorsConfiguration(request);
        
        assertNotNull(configForProd);
        assertTrue(configForProd.getAllowedOrigins().contains(prodOrigin));
        
        // Test with unknown origin
        when(request.getHeader("Origin")).thenReturn("https://unknown.example.com");
        CorsConfiguration configForUnknown = corsConfigSource.getCorsConfiguration(request);
        
        assertNotNull(configForUnknown);
        assertTrue(configForUnknown.getAllowedOrigins() == null || configForUnknown.getAllowedOrigins().isEmpty());
    }

    @Test
    void normalizeOrigin_ShouldHandleEdgeCases() throws Exception {
        // Access private method using reflection
        java.lang.reflect.Method normalizeOriginMethod = 
            SecurityConfig.class.getDeclaredMethod("normalizeOrigin", String.class);
        normalizeOriginMethod.setAccessible(true);
        
        // Test null input
        assertNull(normalizeOriginMethod.invoke(securityConfig, new Object[]{null}));
        
        // Test empty string
        assertEquals("", normalizeOriginMethod.invoke(securityConfig, ""));
        
        // Test with trailing slash
        assertEquals("http://example.com", normalizeOriginMethod.invoke(securityConfig, "http://example.com/"));
        
        // Test with whitespace
        assertEquals("http://example.com", normalizeOriginMethod.invoke(securityConfig, "  http://example.com  "));
        
        // Test with trailing whitespace and slash
        assertEquals("http://example.com", normalizeOriginMethod.invoke(securityConfig, "  http://example.com/  "));
    }
} 