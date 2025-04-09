package stud.ntnu.no.backend.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import stud.ntnu.no.backend.common.security.util.JwtUtils;

/**
 * Filter for authenticating requests using JWT tokens.
 * <p>
 * This filter extracts JWT tokens from cookies, validates them, and sets the authentication context
 * if valid.
 * </p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private static final String JWT_COOKIE_NAME = "jwt";

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsService userDetailsService;

  /**
   * Filters incoming requests to authenticate users based on JWT tokens.
   *
   * @param request     the HTTP request
   * @param response    the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException      if an I/O error occurs
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    logger.debug("Processing request to {}: {}", request.getMethod(), request.getRequestURI());

    try {
      String jwt = getJwtFromCookies(request);
      logger.debug("JWT from cookies: {}", jwt != null ? "Present" : "Not present");

      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String username = jwtUtils.getUsernameFromToken(jwt);
        logger.debug("Username from token: {}", username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("Authentication set for user: {}", username);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Retrieves the JWT token from cookies.
   *
   * @param request the HTTP request
   * @return the JWT token or null if not found
   */
  private String getJwtFromCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      logger.debug("No cookies found in request");
      return null;
    }

    return Arrays.stream(cookies)
        .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }
}