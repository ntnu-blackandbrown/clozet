package stud.ntnu.no.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

@TestConfiguration
@Profile("admin-user-test")
public class AdminUserTestInitializer {

    @Bean
    CommandLineRunner initAdminOnly(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.deleteAll();
            
            User admin = new User();
            admin.setUsername("Admin");
            admin.setPasswordHash(passwordEncoder.encode("Admin1234"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("Administrator");
            admin.setRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
        };
    }
}