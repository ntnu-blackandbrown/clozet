package stud.ntnu.no.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import stud.ntnu.no.backend.user.entity.User;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF and allow all requests for testing
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        
        return http.build();
    }

    // Add mock beans for JWT components
    @Bean
    public stud.ntnu.no.backend.common.security.util.JwtUtils jwtUtils() {
        // Return a mock or minimal implementation of JwtUtils
        return new stud.ntnu.no.backend.common.security.util.JwtUtils() {
            // Implement necessary methods with test behavior
        };
    }

    // In your TestSecurityConfig class:
    @Bean
    public UserDetailsService userDetailsService() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        return username -> (org.springframework.security.core.userdetails.UserDetails) testUser;
    }
}