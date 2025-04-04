package stud.ntnu.no.backend.favorite.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import stud.ntnu.no.backend.common.security.util.JwtUtils;

/**
 * Test security configuration for controller tests.
 * This configuration permits all requests without authentication for testing purposes.
 * It also provides mock beans for security components required by the application.
 */
@Configuration
@EnableWebSecurity
public class TestSecurityConfig {

    /**
     * Mock JwtUtils bean for testing
     */
    @MockBean
    private JwtUtils jwtUtils;

    /**
     * Mock UserDetailsService bean for testing
     */
    @MockBean
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
            );
        
        return http.build();
    }
} 