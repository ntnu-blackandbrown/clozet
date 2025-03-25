package stud.ntnu.no.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import stud.ntnu.no.backend.User.Model.User;
import stud.ntnu.no.backend.User.Repository.UserRepository;

@Configuration
public class DatabaseInitializer {

  @Bean
  CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
      // Slett alle eksisterende brukere (og testdata) før vi setter inn nye data
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
