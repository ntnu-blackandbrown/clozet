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
import stud.ntnu.no.backend.itemimage.repository.ItemImageRepository;
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.category.repository.CategoryRepository;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.favorite.repository.FavoriteRepository;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.location.repository.LocationRepository;
import stud.ntnu.no.backend.message.dto.CreateMessageRequest;
import stud.ntnu.no.backend.message.dto.MessageDTO;
import stud.ntnu.no.backend.message.entity.Message;
import stud.ntnu.no.backend.message.repository.MessageRepository;
import stud.ntnu.no.backend.message.service.MessageService;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.transaction.repository.TransactionRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.PasswordResetTokenRepository;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.user.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.*;

@Configuration
@Profile("!test")
public class DatabaseInitializer {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private LocationRepository locationRepository;
  @Autowired private ItemRepository itemRepository;
  @Autowired private ItemImageRepository itemImageRepository;
  @Autowired private MessageRepository messageRepository;
  @Autowired private FavoriteRepository favoriteRepository;
  @Autowired private TransactionRepository transactionRepository;
  @Autowired private ShippingOptionRepository shippingOptionRepository;
  @Autowired private VerificationTokenRepository verificationTokenRepository;
  @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;

  @Bean
  @Transactional
  public CommandLineRunner initDatabase() {
    return args -> {
      cleanDatabase();

      User seller = createUser("seller@demo.com", "seller", "Seller");
      User buyer = createUser("buyer@demo.com", "buyer", "Buyer");

      Category category = createCategory("Clothing", "All clothing items");
      Location location = createLocation("Oslo", "Oslo", 59.9139, 10.7522);

      for (int i = 0; i < 5; i++) {
        Item item = createItem(seller, category, location, "Demo Item " + i);
        createItemImage(item, "https://images.unsplash.com/photo-1576566588028-4147f3842f27?q=80&w=1000", true);
      }

      logger.info("✅ Simple database init complete.");
    };
  }

  protected void cleanDatabase() {
    logger.info("Cleaning database - removing existing records");
  
    // Delete referencing entities first
    messageRepository.deleteAllInBatch();     // 💬 uses items
    favoriteRepository.deleteAllInBatch();    // 💗 might also reference items
    transactionRepository.deleteAllInBatch(); // 💳 references items
    
    itemImageRepository.deleteAllInBatch();
    itemRepository.deleteAllInBatch();
  
    shippingOptionRepository.deleteAllInBatch();
    locationRepository.deleteAllInBatch();
    categoryRepository.deleteAllInBatch();
    
    verificationTokenRepository.deleteAllInBatch();
    passwordResetTokenRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  
    logger.info("Database cleaned successfully");
  }
  

  private User createUser(String email, String username, String role) {
    User user = new User();
    user.setEmail(email);
    user.setUsername(username);
    user.setPasswordHash(passwordEncoder.encode("Password123"));
    user.setFirstName(username);
    user.setLastName("User");
    user.setRole("ROLE_USER");
    user.setActive(true);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(user);
  }

  private Category createCategory(String name, String desc) {
    Category c = new Category();
    c.setName(name);
    c.setDescription(desc);
    c.setCreatedAt(LocalDateTime.now());
    c.setUpdatedAt(LocalDateTime.now());
    return categoryRepository.save(c);
  }

  private Location createLocation(String city, String region, double lat, double lng) {
    Location l = new Location();
    l.setCity(city);
    l.setRegion(region);
    l.setLatitude(lat);
    l.setLongitude(lng);
    return locationRepository.save(l);
  }

  private Item createItem(User seller, Category cat, Location loc, String title) {
    Item i = new Item();
    i.setSeller(seller);
    i.setCategory(cat);
    i.setLocation(loc);
    i.setTitle(title);
    i.setShortDescription("Short desc for " + title);
    i.setLongDescription("Long description with size, color, and brand info.");
    i.setPrice(199.0);
    i.setCondition("New");
    i.setSize("M");
    i.setBrand("Zara");
    i.setColor("Black");
    i.setAvailable(true);
    i.setVippsPaymentEnabled(true);
    i.setLatitude(loc.getLatitude());
    i.setLongitude(loc.getLongitude());
    i.setCreatedAt(LocalDateTime.now());
    i.setUpdatedAt(LocalDateTime.now());
    return itemRepository.save(i);
  }

  private void createItemImage(Item item, String url, boolean isPrimary) {
    ItemImage img = new ItemImage();
    img.setItem(item);
    img.setImageUrl(url);
    img.setPrimary(isPrimary);
    img.setDisplayOrder(0);
    itemImageRepository.save(img);
  }
}
