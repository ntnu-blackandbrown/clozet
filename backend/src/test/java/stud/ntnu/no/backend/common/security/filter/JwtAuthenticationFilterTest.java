package stud.ntnu.no.backend.common.security.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import stud.ntnu.no.backend.common.security.util.JwtUtils;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtUtils jwtUtils;

  @Mock private UserDetailsService userDetailsService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Mock private UserDetails userDetails;

  @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

  private static final String VALID_TOKEN = "valid.jwt.token";
  private static final String USERNAME = "testuser";

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_WithValidToken_ShouldAuthenticate() throws ServletException, IOException {
    // Given
    Cookie jwtCookie = new Cookie("jwt", VALID_TOKEN);
    Cookie[] cookies = {jwtCookie};

    when(request.getCookies()).thenReturn(cookies);
    when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenReturn(true);
    when(jwtUtils.getUsernameFromToken(VALID_TOKEN)).thenReturn(USERNAME);
    when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
    when(userDetails.getUsername()).thenReturn(USERNAME);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(jwtUtils).validateJwtToken(VALID_TOKEN);
    verify(jwtUtils).getUsernameFromToken(VALID_TOKEN);
    verify(userDetailsService).loadUserByUsername(USERNAME);
    verify(filterChain).doFilter(request, response);

    // Verify authentication is set in SecurityContext
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertEquals(USERNAME, SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @Test
  void doFilterInternal_WithInvalidToken_ShouldNotAuthenticate()
      throws ServletException, IOException {
    // Given
    Cookie jwtCookie = new Cookie("jwt", "invalid.token");
    Cookie[] cookies = {jwtCookie};

    when(request.getCookies()).thenReturn(cookies);
    when(jwtUtils.validateJwtToken("invalid.token")).thenReturn(false);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(jwtUtils).validateJwtToken("invalid.token");
    verify(jwtUtils, never()).getUsernameFromToken(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);

    // Verify no authentication is set
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void doFilterInternal_WithoutCookies_ShouldNotAuthenticate()
      throws ServletException, IOException {
    // Given
    when(request.getCookies()).thenReturn(null);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(jwtUtils, never()).validateJwtToken(anyString());
    verify(jwtUtils, never()).getUsernameFromToken(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);

    // Verify no authentication is set
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void doFilterInternal_WithoutJwtCookie_ShouldNotAuthenticate()
      throws ServletException, IOException {
    // Given
    Cookie otherCookie = new Cookie("other", "value");
    Cookie[] cookies = {otherCookie};

    when(request.getCookies()).thenReturn(cookies);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(jwtUtils, never()).validateJwtToken(anyString());
    verify(jwtUtils, never()).getUsernameFromToken(anyString());
    verify(userDetailsService, never()).loadUserByUsername(anyString());
    verify(filterChain).doFilter(request, response);

    // Verify no authentication is set
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void doFilterInternal_WithExceptionDuringProcessing_ShouldContinueFilterChain()
      throws ServletException, IOException {
    // Given
    Cookie jwtCookie = new Cookie("jwt", VALID_TOKEN);
    Cookie[] cookies = {jwtCookie};

    when(request.getCookies()).thenReturn(cookies);
    when(jwtUtils.validateJwtToken(VALID_TOKEN)).thenThrow(new RuntimeException("Test exception"));

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);

    // Verify no authentication is set
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
