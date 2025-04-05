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
import stud.ntnu.no.backend.favorite.repository.FavoriteRepository;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.location.repository.LocationRepository;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.service.MessageService;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.PasswordResetTokenRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
  private FavoriteRepository favoriteRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MessageService messageService;

  @Bean
  @Transactional
  public CommandLineRunner initDatabase() {
    return args -> {
      logger.info("Starting database initialization...");

      try {
        // Clean the database
        cleanDatabase();

        // Create Admin, Seller, and Buyer users
        createAdminUser();
        User seller = createSeller();
        User buyer = createBuyer();

        // Create basic entities (Category, Location, ShippingOption)
        createBasicEntities();

        // Create items for the seller
        List<Item> sellerItems = createItemsForUser(seller, 10);

        // Create a conversation between buyer and seller about the first item
        createConversation(seller, buyer, sellerItems.get(0));

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
    try {
      favoriteRepository.deleteAllInBatch();
      itemRepository.deleteAllInBatch();
      verificationTokenRepository.deleteAllInBatch();
      passwordResetTokenRepository.deleteAllInBatch();
      userRepository.deleteAllInBatch();
    } catch (Exception e) {
      logger.error("Error cleaning database", e);
    }
  }

  @Transactional
  protected void createAdminUser() {
    logger.info("Creating admin user");
    Optional<User> existingAdmin = userRepository.findByEmail("clozet.adm.demo@gmail.com");
    if (existingAdmin.isPresent()) {
      logger.info("Admin user already exists, skipping creation");
      return;
    }

    User admin = new User();
    admin.setUsername("Admin");
    admin.setPasswordHash(passwordEncoder.encode("Admin1234"));
    admin.setEmail("clozet.adm.demo@gmail.com");
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
  protected User createSeller() {
    logger.info("Creating seller user");
    Optional<User> existingSeller = userRepository.findByEmail("clozet.Seller.demo@gmail.com");
    if (existingSeller.isPresent()) {
      logger.info("Seller user already exists, using existing one");
      return existingSeller.get();
    }

    User seller = new User();
    seller.setUsername("demoSeller");
    seller.setPasswordHash(passwordEncoder.encode("Clozet-Seller-Password"));
    seller.setEmail("clozet.Seller.demo@gmail.com");
    seller.setFirstName("Demo");
    seller.setLastName("Seller");
    seller.setRole("ROLE_USER");
    seller.setActive(true);
    seller.setCreatedAt(LocalDateTime.now());
    seller.setUpdatedAt(LocalDateTime.now());

    User savedSeller = userRepository.save(seller);
    logger.info("Seller user created successfully");
    return savedSeller;
  }

  @Transactional
  protected User createBuyer() {
    logger.info("Creating buyer user");
    Optional<User> existingBuyer = userRepository.findByEmail("clozet.buyer.demo@gmail.com");
    if (existingBuyer.isPresent()) {
      logger.info("Buyer user already exists, using existing one");
      return existingBuyer.get();
    }

    User buyer = new User();
    buyer.setUsername("demoBuyer");
    buyer.setPasswordHash(passwordEncoder.encode("Clozet-Buyer-Password"));
    buyer.setEmail("clozet.buyer.demo@gmail.com");
    buyer.setFirstName("Demo");
    buyer.setLastName("Buyer");
    buyer.setRole("ROLE_USER");
    buyer.setActive(true);
    buyer.setCreatedAt(LocalDateTime.now());
    buyer.setUpdatedAt(LocalDateTime.now());

    User savedBuyer = userRepository.save(buyer);
    logger.info("Buyer user created successfully");
    return savedBuyer;
  }

  @Transactional
  protected void createBasicEntities() {
    if (categoryRepository.count() == 0) {
      Category category = new Category();
      category.setName("General");
      category.setDescription("General category for items");
      categoryRepository.save(category);
      logger.info("Default category created");
    }

    if (locationRepository.count() == 0) {
      Location location = new Location();
      location.setCity("Trondheim");
      location.setRegion("Trøndelag");
      location.setLatitude(63.4305);
      location.setLongitude(10.3951);
      locationRepository.save(location);
      logger.info("Default location created");
    }

    if (shippingOptionRepository.count() == 0) {
      ShippingOption shippingOption = new ShippingOption();
      shippingOption.setName("Standard Shipping");
      shippingOption.setDescription("Regular shipping option");
      shippingOptionRepository.save(shippingOption);
      logger.info("Default shipping option created");
    }
  }

  @Transactional
  protected List<Item> createItemsForUser(User user, int count) {
    logger.info("Creating {} items for user {}", count, user.getUsername());

    List<Item> createdItems = new ArrayList<>();

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
      item.setPrice(50.0 + random.nextInt(450)); // Price between 50 and 500
      item.setCategory(category);
      item.setSeller(user);
      item.setLocation(location);
      item.setShippingOption(shippingOption);
      item.setLatitude(63.4 + random.nextDouble() * 0.1);
      item.setLongitude(10.4 + random.nextDouble() * 0.1);
      item.setCondition(conditions[random.nextInt(conditions.length)]);
      item.setSize(sizes[random.nextInt(sizes.length)]);
      item.setBrand(brands[random.nextInt(brands.length)]);
      item.setColor(colors[random.nextInt(colors.length)]);
      item.setAvailable(true);
      item.setVippsPaymentEnabled(random.nextBoolean());
      item.setCreatedAt(LocalDateTime.now());
      item.setUpdatedAt(LocalDateTime.now());

      createdItems.add(itemRepository.save(item));
    }

    logger.info("Successfully created {} items for user {}", count, user.getUsername());
    return createdItems;
  }

  @Transactional
  protected void createConversation(User seller, User buyer, Item item) {
    logger.info("Creating conversation between seller {} and buyer {} for item {}",
        seller.getUsername(), buyer.getUsername(), item.getTitle());

    // First message: from buyer to seller
    CreateMessageRequest buyerMessageRequest = new CreateMessageRequest(
        buyer.getId().toString(),
        seller.getId().toString(),
        "Hi! I'm interested in your item '" + item.getTitle() + "'. Is it still available?",
        LocalDateTime.now()
    );
    MessageDTO buyerMessage = messageService.createMessage(buyerMessageRequest);

    // Follow-up message: from seller to buyer, 5 minutes later
    CreateMessageRequest sellerMessageRequest = new CreateMessageRequest(
        seller.getId().toString(),
        buyer.getId().toString(),
        "Hello! Thanks for your interest. Yes, it's still available. Let me know if you have any other questions.",
        LocalDateTime.now().plusMinutes(5)
    );
    MessageDTO sellerMessage = messageService.createMessage(sellerMessageRequest);

    logger.info("Conversation created with messages: {} and {}", buyerMessage.getId(), sellerMessage.getId());
  }
}
