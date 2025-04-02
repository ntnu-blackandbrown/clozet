package stud.ntnu.no.backend.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.PasswordResetTokenRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
@Profile("!test")
public class DatabaseInitializer {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

  @Autowired
  private PasswordResetTokenRepository passwordResetTokenRepository;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Bean
  @Transactional
  public CommandLineRunner initDatabase() {
    return args -> {
      logger.info("Starting database initialization...");

      try {
        // Clean database first
        cleanDatabase();

        // Create admin user
        createAdminUser();

        // Initialize additional data if needed
        // initializeOtherData();

        logger.info("Database initialization completed successfully");
      } catch (Exception e) {
        logger.error("Database initialization failed", e);
        throw e;
      }
    };
  }

  @Transactional
  protected void cleanDatabase() {
    logger.info("Cleaning database - removing existing records");

    // Delete dependent entities first to avoid foreign key constraints
    logger.info("Deleting verification tokens");
    verificationTokenRepository.deleteAll();

    logger.info("Deleting password reset tokens");
    passwordResetTokenRepository.deleteAll();

    // Then delete users
    logger.info("Deleting users");
    userRepository.deleteAll();
  }

  @Transactional
  protected void createAdminUser() {
    logger.info("Creating admin user");

    Optional<User> existingAdmin = userRepository.findByEmail("admin@example.com");
    if (existingAdmin.isPresent()) {
      logger.info("Admin user already exists, skipping creation");
      return;
    }

    User admin = new User();
    admin.setUsername("Admin");
    admin.setPasswordHash(passwordEncoder.encode("Admin1234"));
    admin.setEmail("admin@example.com");
    admin.setFirstName("Admin");
    admin.setLastName("Administrator");
    admin.setRole("ADMIN");
    admin.setActive(true);
    admin.setCreatedAt(LocalDateTime.now());
    admin.setUpdatedAt(LocalDateTime.now());

    userRepository.save(admin);
    logger.info("Admin user created successfully");
  }
}