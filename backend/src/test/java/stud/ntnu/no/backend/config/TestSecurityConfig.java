package stud.ntnu.no.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import stud.ntnu.no.backend.common.security.util.JwtUtils;

import static org.mockito.Mockito.*;
import static org.springframework.security.core.userdetails.User.withUsername;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
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
        when(mockJwtUtils.getUsernameFromToken(anyString())).thenReturn("testuser");
        return mockJwtUtils;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                if (!username.equals("testuser")) {
                    throw new UsernameNotFoundException("User not found");
                }

                return withUsername("testuser")
                    .password(passwordEncoder().encode("password"))
                    .roles("USER")
                    .build();
            }
        };
    }


}
