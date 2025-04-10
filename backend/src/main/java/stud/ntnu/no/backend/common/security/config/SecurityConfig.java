package stud.ntnu.no.backend.common.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import stud.ntnu.no.backend.common.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configuration class for setting up security filters and authentication.
 * <p>
 * This class configures CORS, CSRF, session management, and exception handling.
 * It also sets up the security filter chain and password encoding.
 * </p>
 *
 * 
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        logger.info("SecurityConfig initialized");
    }

    /**
     * Normalizes the origin by trimming and removing any trailing slashes.
     * 
     * @param origin the origin to normalize
     * @return the normalized origin, or null if origin is null
     */
    private String normalizeOrigin(String origin) {
        if (origin == null) {
            return null;
        }
        String trimmed = origin.trim();
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }

    /**
     * Configures CORS settings for the application.
     * 
     * @return the CORS configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CORS settings");
        
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Instead of using patterns, we'll validate origins in the configure method
        corsConfig.setAllowedOrigins(Collections.emptyList()); // We'll manually validate origins
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*")); // Allow any origin for initial processing
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfig.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "Accept", "X-Requested-With", "remember-me"));
        corsConfig.setExposedHeaders(Arrays.asList("Content-Type"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                String origin = request.getHeader("Origin");
                logger.info("Request origin: {}", origin);
                
                origin = normalizeOrigin(origin);
                
                // Create a copy to modify for this specific request
                CorsConfiguration config = corsConfig.applyPermitDefaultValues();
                
                // Only allow specific origins
                if (origin != null && (
                        origin.equals("http://localhost:5173") ||
                        origin.equals("https://clozet.netlify.app"))) {
                    logger.info("Setting Access-Control-Allow-Origin to: {}", origin);
                    config.setAllowedOrigins(Collections.singletonList(origin));
                }
                
                return config;
            }
        };
    }

    /**
     * Configures the security filter chain.
     * 
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain");

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exc -> exc
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .authorizeHttpRequests(auth -> auth
                // Allow only authentication-related endpoints without authentication
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // Keeping H2 console accessible
                .requestMatchers("/api/prod-test/verification").permitAll() // Verification test endpoint
                .requestMatchers("/ws/**").permitAll() // WebSocket endpoints for testing
                // Require authentication for all other API endpoints
                .requestMatchers("/api/categories/top-five").permitAll()
                .requestMatchers("/api/marketplace/items").permitAll()
                .requestMatchers("/api/items/**").permitAll()
                .requestMatchers("/api/me").permitAll()
                .requestMatchers("api/images/item/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Tillat at H2-console vises
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    /**
     * Provides the authentication manager bean.
     * 
     * @param authenticationConfiguration the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs during retrieval
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Provides the password encoder bean.
     * 
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}