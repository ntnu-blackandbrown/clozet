package stud.ntnu.no.backend.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import stud.ntnu.no.backend.common.security.util.JwtUtils;
import stud.ntnu.no.backend.common.security.util.JwtTestUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TestSecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void testSecurityFilterChain() {
        assertNotNull(securityFilterChain);
    }

    @Test
    void testPasswordEncoder() {
        assertTrue(passwordEncoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder);
    }

    @Test
    void testJwtUtils() {
        // Setup the mock behavior
        when(jwtUtils.getUsernameFromToken(anyString())).thenReturn("testuser");
        
        // Test the mock
        String token = "any-token-string";
        assertEquals("testuser", jwtUtils.getUsernameFromToken(token));
        
        // Verify the mock was called
        verify(jwtUtils).getUsernameFromToken(token);
    }
} 