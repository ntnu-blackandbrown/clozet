package stud.ntnu.no.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import stud.ntnu.no.backend.User.User;
import stud.ntnu.no.backend.User.UserRepository;

/**
 * Test database initializer that sets up the database for testing.
 * This class is active only with the "test" profile.
 */
@Configuration
@Profile("test")
public class TestDatabaseInitializer {

    /**
     * Initializes the test database with predefined users.
     * This overrides the main DatabaseInitializer in test environment.
     */
    @Bean
    CommandLineRunner initTestDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Delete all existing data
            userRepository.deleteAll();
            
            // Create admin user similar to the production database setup
            User admin = new User();
            admin.setUsername("Admin");
            admin.setPasswordHash(passwordEncoder.encode("Admin1234"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("Administrator");
            admin.setRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
            
            // Create a regular test user
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPasswordHash(passwordEncoder.encode("testPassword"));
            testUser.setEmail("test@example.com");
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setRole("USER");
            testUser.setActive(true);
            userRepository.save(testUser);
        };
    }
} 