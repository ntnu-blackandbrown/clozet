package stud.ntnu.no.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for web security.
 * <p>
 * Configures security settings for WebSocket endpoints.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  /**
   * Configures the security filter chain for WebSocket endpoints.
   *
   * @param http the HttpSecurity to configure
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  @Order(1) // Higher precedence than the default filter chain
  public SecurityFilterChain webSocketSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/ws/**")  // Apply only to WebSocket endpoints
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().permitAll());

    return http.build();
  }
}