package stud.ntnu.no.backend.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;
import java.util.Arrays;

/**
 * Filter for authenticating requests using JWT tokens.
 * <p>
 * This filter extracts JWT tokens from cookies, validates them, and sets the
 * authentication context if valid.
 * </p>
 * 
 *
 * 
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
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        logger.info("Processing request to {}: {}", request.getMethod(), requestUri);
        
        // Add detailed logging for /api/me endpoint
        if ("/api/me".equals(requestUri)) {
            logger.info("Processing /api/me request");
            logger.info("Authentication before filter: {}", SecurityContextHolder.getContext().getAuthentication());
        }

        try {
            String jwt = getJwtFromCookies(request);
            logger.info("JWT from cookies: {}", jwt != null ? "Present" : "Not present");

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromToken(jwt);
                logger.info("Username from token: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentication set for user: {}", username);
            } else if ("/api/me".equals(requestUri)) {
                logger.info("No valid JWT found for /api/me request");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            if ("/api/me".equals(requestUri)) {
                logger.error("Authentication error for /api/me: {}", e.getMessage(), e);
            }
        }

        if ("/api/me".equals(requestUri)) {
            logger.info("Authentication after filter processing: {}", SecurityContextHolder.getContext().getAuthentication());
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
            logger.info("No cookies found in request");
            return null;
        }

        return Arrays.stream(cookies)
                .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}