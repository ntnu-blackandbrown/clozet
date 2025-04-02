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

import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.category.repository.CategoryRepository;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.location.repository.LocationRepository;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.PasswordResetTokenRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

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
  private ItemRepository itemRepository;
  
  @Autowired
  private CategoryRepository categoryRepository;
  
  @Autowired
  private LocationRepository locationRepository;
  
  @Autowired
  private ShippingOptionRepository shippingOptionRepository;

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
        
        // Create regular user
        User regularUser = createRegularUser();
        
        // Create categories, locations, and shipping options if they don't exist
        createBasicEntities();
        
        // Create 10 items for the regular user
        createItemsForUser(regularUser, 10);

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
    logger.info("Deleting items");
    itemRepository.deleteAll();
    
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
  
  @Transactional
  protected User createRegularUser() {
    logger.info("Creating regular user");
    
    Optional<User> existingUser = userRepository.findByEmail("user@example.com");
    if (existingUser.isPresent()) {
      logger.info("Regular user already exists, using existing one");
      return existingUser.get();
    }
    
    User user = new User();
    user.setUsername("user");
    user.setPasswordHash(passwordEncoder.encode("User1234"));
    user.setEmail("user@example.com");
    user.setFirstName("Regular");
    user.setLastName("User");
    user.setRole("ROLE_USER");
    user.setActive(true);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    
    User savedUser = userRepository.save(user);
    logger.info("Regular user created successfully");
    return savedUser;
  }

  @Transactional
  protected void createBasicEntities() {
    // Create a default category if none exists
    if (categoryRepository.count() == 0) {
      Category category = new Category();
      category.setName("General");
      category.setDescription("General category for items");
      categoryRepository.save(category);
      logger.info("Default category created");
    }

    // Create a default location if none exists
    if (locationRepository.count() == 0) {
      Location location = new Location();
      location.setCity("Trondheim");
      location.setRegion("Trøndelag"); // Setting required region field
      location.setLatitude(63.4305); // Trondheim latitude
      location.setLongitude(10.3951); // Trondheim longitude
      locationRepository.save(location);
      logger.info("Default location created");
    }

    // Create a default shipping option if none exists
    if (shippingOptionRepository.count() == 0) {
      ShippingOption shippingOption = new ShippingOption();
      shippingOption.setName("Standard Shipping");
      shippingOption.setDescription("Regular shipping option");
      shippingOptionRepository.save(shippingOption);
      logger.info("Default shipping option created");
    }
  }
  
  @Transactional
  protected void createItemsForUser(User user, int count) {
    logger.info("Creating {} items for user {}", count, user.getUsername());
    
    // Get default entities
    Category category = categoryRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new RuntimeException("No category found"));
        
    Location location = locationRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new RuntimeException("No location found"));
        
    ShippingOption shippingOption = shippingOptionRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new RuntimeException("No shipping option found"));
    
    Random random = new Random();
    String[] conditions = {"New", "Used - like new", "Used - good", "Used - fair"};
    String[] brands = {"Nike", "Adidas", "Puma", "Reebok", "Under Armour"};
    String[] colors = {"Red", "Blue", "Green", "Black", "White", "Yellow"};
    String[] sizes = {"XS", "S", "M", "L", "XL"};
    
    for (int i = 0; i < count; i++) {
      Item item = new Item();
      item.setTitle("Item " + (i + 1));
      item.setShortDescription("Short description for item " + (i + 1));
      item.setLongDescription("This is a detailed description for item " + (i + 1) + ". It provides more information about the item's features and condition.");
      item.setPrice(50.0 + random.nextInt(450)); // Price between $50 and $500
      item.setCategory(category);
      item.setSeller(user);
      item.setLocation(location);
      item.setShippingOption(shippingOption);
      item.setLatitude(63.4 + random.nextDouble() * 0.1); // Random latitude around Trondheim
      item.setLongitude(10.4 + random.nextDouble() * 0.1); // Random longitude around Trondheim
      item.setCondition(conditions[random.nextInt(conditions.length)]);
      item.setSize(sizes[random.nextInt(sizes.length)]);
      item.setBrand(brands[random.nextInt(brands.length)]);
      item.setColor(colors[random.nextInt(colors.length)]);
      item.setAvailable(true);
      item.setVippsPaymentEnabled(random.nextBoolean());
      item.setCreatedAt(LocalDateTime.now());
      item.setUpdatedAt(LocalDateTime.now());
      
      itemRepository.save(item);
    }
    
    logger.info("Successfully created {} items for user {}", count, user.getUsername());
  }
}