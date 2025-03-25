package stud.ntnu.no.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * General test configuration for all tests.
 * This configuration provides beans needed for testing and is activated
 * with the "test" profile.
 */
@TestConfiguration
@Profile("test")
@ActiveProfiles("test")
public class TestConfig {

    /**
     * Password encoder for test environment.
     * We use the same encoder as production for consistency.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Overrides the CommandLineRunner bean from main DatabaseInitializer
     * to prevent it from running in tests. TestDatabaseInitializer will be used instead.
     */
    @Bean
    @Profile("test")
    public boolean disableMainDatabaseInitializer() {
        return true;
    }
} 