package stud.ntnu.no.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

@TestConfiguration
@ActiveProfiles("test")
public class TestDatabaseInitializer {

    @Bean
    CommandLineRunner testInitDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println(">> TestDatabaseInitializer triggered: Sletter alle brukere og setter inn Admin.");
            // Slett alle eksisterende brukere for å sikre et konsistent testmiljø
            userRepository.deleteAll();
            System.out.println(">> Alle brukere slettet. Antall: " + userRepository.count());

            // Legg inn kun admin-brukeren for testformål
            User admin = new User();
            admin.setUsername("Admin");
            admin.setPasswordHash(passwordEncoder.encode("Admin1234"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("Administrator");
            admin.setRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println(">> Admin-brukeren er lagret. Antall brukere nå: " + userRepository.count());
        };
    }
}
