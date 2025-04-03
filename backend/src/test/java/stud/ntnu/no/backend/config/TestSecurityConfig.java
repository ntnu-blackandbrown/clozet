package stud.ntnu.no.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import stud.ntnu.no.backend.common.security.util.JwtUtils;
import stud.ntnu.no.backend.user.entity.User;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.userdetails.User.withUsername;

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtils jwtUtils() {
        JwtUtils mockJwtUtils = mock(JwtUtils.class);
        when(mockJwtUtils.getUsernameFromToken(org.mockito.ArgumentMatchers.anyString())).thenReturn("testuser");
        return mockJwtUtils;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserBuilder builder = withUsername("testuser");
                builder.password(passwordEncoder().encode("password"));
                builder.roles("USER");
                return builder.build();
            }
        };
    }

    @Bean
    public User mockAuthenticatedUser() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setActive(true);
        testUser.setRole("ROLE_USER");
        return testUser;
    }
}