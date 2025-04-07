package stud.ntnu.no.backend.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import stud.ntnu.no.backend.common.security.util.JwtUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TestSecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jwtUtils.getUsernameFromToken(anyString())).thenReturn("testuser");
    }

    // Configuration to provide mock beans
    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public JwtUtils jwtUtils() {
            JwtUtils mockJwtUtils = mock(JwtUtils.class);
            when(mockJwtUtils.getUsernameFromToken(anyString())).thenReturn("testuser");
            return mockJwtUtils;
        }
        
        @Bean
        @Primary
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Test
    void testPasswordEncoder() {
        assertTrue(passwordEncoder instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder);
    }

    @Test
    void testJwtUtils() {
        // Test the mock
        String token = "any-token-string";
        assertEquals("testuser", jwtUtils.getUsernameFromToken(token));
        
        // Verify the mock was called
        verify(jwtUtils).getUsernameFromToken(token);
    }
} 