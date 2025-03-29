package stud.ntnu.no.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

@Configuration
@Profile("!test") // Denne initializeren kjøres ikke når testprofilen er aktiv
public class DatabaseInitializer {

  @Bean
  CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
      // Slett alle eksisterende brukere (inkludert testdata) før vi setter inn admin-brukeren
      userRepository.deleteAll();

      // Legg inn admin-brukeren på nytt
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
