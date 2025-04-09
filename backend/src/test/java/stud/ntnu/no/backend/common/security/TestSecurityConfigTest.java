package stud.ntnu.no.backend.common.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.common.security.util.JwtUtils;

@SpringBootTest(classes = {TestSecurityConfig.class, TestSecurityConfigTest.TestConfig.class})
@ActiveProfiles("test")
class TestSecurityConfigTest {

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private JwtUtils jwtUtils;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(jwtUtils.getUsernameFromToken(anyString())).thenReturn("testuser");
  }

  @Test
  void testPasswordEncoder() {
    assertTrue(
        passwordEncoder
            instanceof org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder);
  }

  @Test
  void testJwtUtils() {
    // Test the mock
    String token = "any-token-string";
    assertEquals("testuser", jwtUtils.getUsernameFromToken(token));

    // Verify the mock was called
    verify(jwtUtils).getUsernameFromToken(token);
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
}
