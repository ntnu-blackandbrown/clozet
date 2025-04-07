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

  @Autowired
  private PasswordResetTokenRepository passwordResetTokenRepository;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private ItemImageRepository itemImageRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private ShippingOptionRepository shippingOptionRepository;

  @Autowired
  private FavoriteRepository favoriteRepository;

  @Autowired
  private MessageRepository messageRepository;
  
  @Autowired
  private TransactionRepository transactionRepository;

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
        User seller1 = createSeller();
        User buyer1 = createBuyer();
        
        // Create additional users
        List<User> additionalUsers = createAdditionalUsers(5);
        List<User> allSellers = new ArrayList<>(additionalUsers);
        allSellers.add(seller1);
        
        // Create categories hierarchy
        Map<String, Category> categories = createCategories();
        
        // Create locations
        List<Location> locations = createLocations();
        
        // Create shipping options
        List<ShippingOption> shippingOptions = createShippingOptions();
        
        // Create items for each seller (with different categories)
        Map<User, List<Item>> userItems = new HashMap<>();
        for (User seller : allSellers) {
            List<Item> sellerItems = createItemsForUser(seller, 10, categories, locations, shippingOptions);
            userItems.put(seller, sellerItems);
        }
        
        // Create favorites
        createFavorites(buyer1, userItems);
        
        // Create conversation threads
        createConversations(userItems, allSellers, buyer1);
        
        // Create some transactions
        createTransactions(userItems, buyer1);

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
      // Delete in the correct order to respect foreign key constraints
      favoriteRepository.deleteAllInBatch();
      messageRepository.deleteAllInBatch();
      transactionRepository.deleteAllInBatch();
      itemImageRepository.deleteAllInBatch();
      itemRepository.deleteAllInBatch();
      shippingOptionRepository.deleteAllInBatch();
      locationRepository.deleteAllInBatch();
      categoryRepository.deleteAllInBatch();
      verificationTokenRepository.deleteAllInBatch();
      passwordResetTokenRepository.deleteAllInBatch();
      userRepository.deleteAllInBatch();
      
      logger.info("Database cleaned successfully");
    } catch (Exception e) {
      logger.error("Error cleaning database", e);
      throw e;
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
  protected List<User> createAdditionalUsers(int count) {
    logger.info("Creating {} additional users", count);
    List<User> additionalUsers = new ArrayList<>();
    
    String[] firstNames = {"Emma", "Noah", "Olivia", "Liam", "Ava", "William", "Sophia", "James", "Isabella", "Oliver"};
    String[] lastNames = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor"};
    
    for (int i = 0; i < count; i++) {
      String firstName = firstNames[i % firstNames.length];
      String lastName = lastNames[i % lastNames.length];
      String username = firstName.toLowerCase() + lastName.toLowerCase() + (i + 1);
      String email = username + "@example.com";
      
      // Check if user already exists
      if (userRepository.findByEmail(email).isPresent()) {
        continue;
      }
      
      User user = new User();
      user.setUsername(username);
      user.setPasswordHash(passwordEncoder.encode("Password123"));
      user.setEmail(email);
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setRole("ROLE_USER");
      user.setActive(true);
      user.setCreatedAt(LocalDateTime.now());
      user.setUpdatedAt(LocalDateTime.now());
      
      additionalUsers.add(userRepository.save(user));
    }
    
    logger.info("Created {} additional users", additionalUsers.size());
    return additionalUsers;
  }

  @Transactional
  protected Map<String, Category> createCategories() {
    logger.info("Creating categories hierarchy");
    Map<String, Category> categories = new HashMap<>();
    LocalDateTime now = LocalDateTime.now();
    
    // Create parent categories
    Category mensCategory = new Category();
    mensCategory.setName("Men's Clothing");
    mensCategory.setDescription("Clothing items for men");
    mensCategory.setCreatedAt(now);
    mensCategory.setUpdatedAt(now);
    mensCategory = categoryRepository.save(mensCategory);
    categories.put("mens", mensCategory);
    
    Category womensCategory = new Category();
    womensCategory.setName("Women's Clothing");
    womensCategory.setDescription("Clothing items for women");
    womensCategory.setCreatedAt(now);
    womensCategory.setUpdatedAt(now);
    womensCategory = categoryRepository.save(womensCategory);
    categories.put("womens", womensCategory);
    
    Category kidsCategory = new Category();
    kidsCategory.setName("Kid's Clothing");
    kidsCategory.setDescription("Clothing items for children");
    kidsCategory.setCreatedAt(now);
    kidsCategory.setUpdatedAt(now);
    kidsCategory = categoryRepository.save(kidsCategory);
    categories.put("kids", kidsCategory);
    
    Category accessoriesCategory = new Category();
    accessoriesCategory.setName("Accessories");
    accessoriesCategory.setDescription("Fashion accessories for all");
    accessoriesCategory.setCreatedAt(now);
    accessoriesCategory.setUpdatedAt(now);
    accessoriesCategory = categoryRepository.save(accessoriesCategory);
    categories.put("accessories", accessoriesCategory);
    
    // Create men's subcategories
    createSubcategory("Men's Tops", "T-shirts, shirts and other tops for men", mensCategory, categories);
    createSubcategory("Men's Pants", "Jeans, trousers and other bottoms for men", mensCategory, categories);
    createSubcategory("Men's Jackets", "Jackets, coats and outerwear for men", mensCategory, categories);
    createSubcategory("Men's Shoes", "Footwear for men", mensCategory, categories);
    createSubcategory("Men's Formal Wear", "Suits and formal attire for men", mensCategory, categories);
    
    // Create women's subcategories
    createSubcategory("Women's Tops", "T-shirts, blouses and other tops for women", womensCategory, categories);
    createSubcategory("Women's Pants", "Jeans, trousers and other bottoms for women", womensCategory, categories);
    createSubcategory("Women's Dresses", "Dresses and skirts for women", womensCategory, categories);
    createSubcategory("Women's Jackets", "Jackets, coats and outerwear for women", womensCategory, categories);
    createSubcategory("Women's Shoes", "Footwear for women", womensCategory, categories);
    
    // Create kids subcategories
    createSubcategory("Boys' Clothing", "Clothing for boys", kidsCategory, categories);
    createSubcategory("Girls' Clothing", "Clothing for girls", kidsCategory, categories);
    createSubcategory("Infant & Toddler", "Clothing for infants and toddlers", kidsCategory, categories);
    
    // Create accessories subcategories
    createSubcategory("Bags & Purses", "Bags, purses and backpacks", accessoriesCategory, categories);
    createSubcategory("Jewelry", "Necklaces, bracelets, rings and other jewelry", accessoriesCategory, categories);
    createSubcategory("Hats & Caps", "Headwear including hats, caps and beanies", accessoriesCategory, categories);
    createSubcategory("Scarves & Gloves", "Scarves, gloves and mittens", accessoriesCategory, categories);
    createSubcategory("Belts", "Fashion belts for all", accessoriesCategory, categories);
    
    logger.info("Created {} categories", categories.size());
    return categories;
  }
  
  private void createSubcategory(String name, String description, Category parent, Map<String, Category> categories) {
    Category subcategory = new Category();
    subcategory.setName(name);
    subcategory.setDescription(description);
    subcategory.setParent(parent);
    subcategory.setCreatedAt(LocalDateTime.now());
    subcategory.setUpdatedAt(LocalDateTime.now());
    subcategory = categoryRepository.save(subcategory);
    categories.put(name.toLowerCase().replace("'", "").replace(" ", "_"), subcategory);
  }

  @Transactional
  protected List<Location> createLocations() {
    logger.info("Creating locations");
    List<Location> locations = new ArrayList<>();
    
    // Norwegian locations with coordinates
    createAndAddLocation(locations, "Oslo", "Oslo", 59.9139, 10.7522);
    createAndAddLocation(locations, "Bergen", "Vestland", 60.3913, 5.3221);
    createAndAddLocation(locations, "Trondheim", "Trøndelag", 63.4305, 10.3951);
    createAndAddLocation(locations, "Stavanger", "Rogaland", 58.9700, 5.7331);
    createAndAddLocation(locations, "Tromsø", "Troms og Finnmark", 69.6489, 18.9551);
    createAndAddLocation(locations, "Kristiansand", "Agder", 58.1599, 8.0182);
    createAndAddLocation(locations, "Ålesund", "Møre og Romsdal", 62.4722, 6.1495);
    createAndAddLocation(locations, "Bodø", "Nordland", 67.2804, 14.4050);
    createAndAddLocation(locations, "Drammen", "Viken", 59.7440, 10.2045);
    createAndAddLocation(locations, "Fredrikstad", "Viken", 59.2181, 10.9298);
    
    logger.info("Created {} locations", locations.size());
    return locations;
  }
  
  private void createAndAddLocation(List<Location> locations, String city, String region, double latitude, double longitude) {
    Location location = new Location();
    location.setCity(city);
    location.setRegion(region);
    location.setLatitude(latitude);
    location.setLongitude(longitude);
    locations.add(locationRepository.save(location));
  }

  @Transactional
  protected List<ShippingOption> createShippingOptions() {
    logger.info("Creating shipping options");
    List<ShippingOption> options = new ArrayList<>();
    
    ShippingOption standardOption = new ShippingOption();
    standardOption.setName("Standard Shipping");
    standardOption.setDescription("Regular delivery within 3-5 business days");
    standardOption.setEstimatedDays(5);
    standardOption.setPrice(49.0);
    standardOption.setTracked(false);
    options.add(shippingOptionRepository.save(standardOption));
    
    ShippingOption expressOption = new ShippingOption();
    expressOption.setName("Express Shipping");
    expressOption.setDescription("Fast delivery within 1-2 business days");
    expressOption.setEstimatedDays(2);
    expressOption.setPrice(99.0);
    expressOption.setTracked(true);
    options.add(shippingOptionRepository.save(expressOption));
    
    ShippingOption pickupOption = new ShippingOption();
    pickupOption.setName("Local Pickup");
    pickupOption.setDescription("Pick up the item directly from the seller");
    pickupOption.setEstimatedDays(1);
    pickupOption.setPrice(0.0);
    pickupOption.setTracked(false);
    options.add(shippingOptionRepository.save(pickupOption));
    
    ShippingOption internationalOption = new ShippingOption();
    internationalOption.setName("International Shipping");
    internationalOption.setDescription("Delivery to countries outside Norway within 7-14 business days");
    internationalOption.setEstimatedDays(14);
    internationalOption.setPrice(199.0);
    internationalOption.setTracked(true);
    options.add(shippingOptionRepository.save(internationalOption));
    
    logger.info("Created {} shipping options", options.size());
    return options;
  }

  @Transactional
  protected List<Item> createItemsForUser(User seller, int count, Map<String, Category> categories, List<Location> locations, List<ShippingOption> shippingOptions) {
    logger.info("Creating {} items for user {}", count, seller.getUsername());
    List<Item> createdItems = new ArrayList<>();
    Random random = new Random();
    
    // Define clothing item properties
    String[] conditions = {"New", "Used - like new", "Used - good", "Used - fair"};
    String[] brands = {"H&M", "Zara", "Nike", "Adidas", "Levi's", "Gucci", "Prada", "The North Face", 
                       "Patagonia", "Ralph Lauren", "Tommy Hilfiger", "Mango", "Uniqlo", "Louis Vuitton"};
    String[] colors = {"Black", "White", "Red", "Blue", "Green", "Yellow", "Purple", "Gray", 
                       "Brown", "Pink", "Orange", "Navy", "Maroon", "Teal", "Lavender", "Beige"};
    String[] sizes = {"XS", "S", "M", "L", "XL", "XXL", "36", "38", "40", "42", "44", "6", "8", "10", "12"};
    
    // Realistic clothing titles and descriptions
    String[][] clothingItems = {
        // Men's clothing items
        {"Men's Classic T-Shirt", "Comfortable everyday t-shirt", "This classic t-shirt is made of 100% cotton, perfect for any casual outfit. Breathable and durable fabric."},
        {"Men's Slim Fit Jeans", "Stylish slim fit denim", "These slim fit jeans offer the perfect balance of style and comfort. Made with premium denim with a touch of stretch."},
        {"Men's Wool Sweater", "Warm winter sweater", "This luxury wool sweater will keep you warm during the coldest days. Features a classic design that never goes out of style."},
        {"Men's Leather Jacket", "Genuine leather biker jacket", "Timeless leather jacket made from genuine leather. Features multiple pockets and adjustable belt at the waist."},
        {"Men's Formal Suit", "Classic business suit", "Professional suit perfect for business meetings and formal events. Tailored fit with premium fabric."},
        {"Men's Winter Parka", "Waterproof winter coat", "Stay warm and dry with this insulated parka. Features waterproof exterior and soft inner lining."},
        {"Men's Running Shoes", "Lightweight athletic shoes", "Designed for maximum performance, these lightweight running shoes feature enhanced cushioning and breathable mesh."},
        {"Men's Button-Up Shirt", "Casual oxford shirt", "Versatile button-up shirt that can be dressed up or down. Made from soft, breathable cotton."},
        
        // Women's clothing items
        {"Women's Blouse", "Elegant silk blouse", "Elegant silk blouse with a flattering cut. Perfect for office wear or evening outings."},
        {"Women's Skinny Jeans", "High-waisted skinny jeans", "These versatile skinny jeans feature a high waist and flattering fit. Stretchy denim ensures all-day comfort."},
        {"Women's Summer Dress", "Floral midi dress", "Beautiful floral print dress with adjustable straps. Light and airy fabric, perfect for warm summer days."},
        {"Women's Wool Coat", "Classic winter coat", "Elegant wool coat with a timeless design. Features a belted waist and collar that can be worn up or down."},
        {"Women's Cardigan", "Soft knit cardigan", "Cozy and versatile cardigan that pairs well with any outfit. Features soft knit fabric and button closure."},
        {"Women's High Heels", "Classic stiletto heels", "Elegant stiletto heels that add a touch of sophistication to any outfit. Features cushioned insole for comfort."},
        {"Women's Leather Boots", "Knee-high leather boots", "These stylish knee-high boots are made of genuine leather with a comfortable block heel. Perfect for fall and winter."},
        {"Women's Activewear Set", "Matching yoga set", "Comfortable and stylish activewear set including leggings and sports bra. Moisture-wicking fabric for maximum comfort."},
        
        // Accessories
        {"Designer Handbag", "Luxury leather handbag", "This designer handbag combines elegance with functionality. Features multiple compartments and adjustable shoulder strap."},
        {"Silver Necklace", "Handcrafted pendant", "Beautiful handcrafted silver necklace with unique pendant design. Adjustable chain length."},
        {"Leather Belt", "Classic leather belt", "Timeless leather belt with classic buckle. Made from genuine leather that will last for years."},
        {"Winter Scarf", "Soft wool blend scarf", "Stay warm with this soft wool blend scarf. Features a classic pattern that complements any winter outfit."},
        {"Baseball Cap", "Adjustable cotton cap", "Casual baseball cap perfect for sunny days. Features adjustable strap for perfect fit."},
        {"Leather Wallet", "Compact bifold wallet", "Sleek leather wallet with multiple card slots and bill compartment. Compact design fits comfortably in any pocket."},
        
        // Kids' clothing
        {"Boys' T-Shirt Set", "3-pack of graphic tees", "Colorful set of three t-shirts with fun graphic prints. Made from soft cotton for all-day comfort."},
        {"Girls' Summer Dress", "Colorful cotton dress", "Adorable summer dress with playful pattern. Lightweight and comfortable for active kids."},
        {"Kid's Winter Jacket", "Warm padded jacket", "Keep your little one warm with this insulated winter jacket. Features hood and easy zip closure."},
        {"Toddler Shoes", "First walking shoes", "Supportive shoes designed for new walkers. Features flexible sole and secure closure."},
    };
    
    // Unsplash image URLs for clothing items (fashion/apparel categories)
    String[] imageUrls = {
        "https://images.unsplash.com/photo-1576566588028-4147f3842f27?q=80&w=1000",
        "https://images.unsplash.com/photo-1551488831-00ddcb6c6bd3?q=80&w=1000",
        "https://images.unsplash.com/photo-1598033129183-c4f50c736f10?q=80&w=1000",
        "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?q=80&w=1000",
        "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?q=80&w=1000",
        "https://images.unsplash.com/photo-1516762689617-e1cffcef479d?q=80&w=1000",
        "https://images.unsplash.com/photo-1556306535-0f09a537f0a3?q=80&w=1000",
        "https://images.unsplash.com/photo-1542272604-787c3835535d?q=80&w=1000",
        "https://images.unsplash.com/photo-1602293589930-45aad59ba3ab?q=80&w=1000",
        "https://images.unsplash.com/photo-1505022610485-0249ba5b3675?q=80&w=1000",
        "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?q=80&w=1000",
        "https://images.unsplash.com/photo-1561052072-1055e5d9f41f?q=80&w=1000",
        "https://images.unsplash.com/photo-1566150905458-1bf1fc113f0d?q=80&w=1000",
        "https://images.unsplash.com/photo-1591369822096-ffd140ec948f?q=80&w=1000",
        "https://images.unsplash.com/photo-1544441893-675973e31985?q=80&w=1000",
        "https://images.unsplash.com/photo-1588117260148-b47818741c74?q=80&w=1000",
        "https://images.unsplash.com/photo-1613461920867-9ea115fee900?q=80&w=1000",
        "https://images.unsplash.com/photo-1618413133517-0e029d333fb4?q=80&w=1000",
        "https://images.unsplash.com/photo-1576871337622-98d48d1cf531?q=80&w=1000",
        "https://images.unsplash.com/photo-1508623662323-33ef159db7f4?q=80&w=1000"
    };
    
    // Associate category keys with appropriate items for more realistic data
    Map<String, List<Integer>> categoryItemIndices = new HashMap<>();
    categoryItemIndices.put("mens_tops", Arrays.asList(0, 5, 7));
    categoryItemIndices.put("mens_pants", Arrays.asList(1));
    categoryItemIndices.put("mens_jackets", Arrays.asList(3, 5));
    categoryItemIndices.put("mens_shoes", Arrays.asList(6));
    categoryItemIndices.put("mens_formal_wear", Arrays.asList(4));
    
    categoryItemIndices.put("womens_tops", Arrays.asList(8, 12));
    categoryItemIndices.put("womens_pants", Arrays.asList(9));
    categoryItemIndices.put("womens_dresses", Arrays.asList(10));
    categoryItemIndices.put("womens_jackets", Arrays.asList(11));
    categoryItemIndices.put("womens_shoes", Arrays.asList(13, 14));
    
    categoryItemIndices.put("boys_clothing", Arrays.asList(24));
    categoryItemIndices.put("girls_clothing", Arrays.asList(25));
    categoryItemIndices.put("infant_&_toddler", Arrays.asList(24));
    
    categoryItemIndices.put("bags_&_purses", Arrays.asList(16));
    categoryItemIndices.put("jewelry", Arrays.asList(17));
    categoryItemIndices.put("hats_&_caps", Arrays.asList(20));
    categoryItemIndices.put("scarves_&_gloves", Arrays.asList(19));
    categoryItemIndices.put("belts", Arrays.asList(18));
    
    // Print array length for debugging
    logger.info("clothingItems array length: {}", clothingItems.length);
    
    for (int i = 0; i < count; i++) {
      // Select a random category
      String[] categoryKeys = categories.keySet().toArray(new String[0]);
      String categoryKey = categoryKeys[random.nextInt(categoryKeys.length)];
      Category category = categories.get(categoryKey);
      
      // Try to match appropriate items to categories
      int itemIndex;
      if (categoryItemIndices.containsKey(categoryKey) && !categoryItemIndices.get(categoryKey).isEmpty()) {
        List<Integer> indices = categoryItemIndices.get(categoryKey);
        itemIndex = indices.get(random.nextInt(indices.size()));
      } else {
        itemIndex = random.nextInt(clothingItems.length);
      }
      
      // Select random location and shipping option
      Location location = locations.get(random.nextInt(locations.size()));
      ShippingOption shippingOption = shippingOptions.get(random.nextInt(shippingOptions.size()));
      
      // Create the item with appropriate data
      Item item = new Item();
      item.setTitle(clothingItems[itemIndex][0] + " - " + colors[random.nextInt(colors.length)]);
      item.setShortDescription(clothingItems[itemIndex][1]);
      item.setLongDescription(clothingItems[itemIndex][2] + " Size: " + sizes[random.nextInt(sizes.length)] + 
                               ". Color: " + colors[random.nextInt(colors.length)] + 
                               ". Brand: " + brands[random.nextInt(brands.length)]);
      item.setPrice(50.0 + random.nextInt(4500) / 10.0); // Price between 50 and 500 with decimal
      item.setCategory(category);
      item.setSeller(seller);
      item.setLocation(location);
      item.setShippingOption(shippingOption);
      
      // Set coordinates with small random offset from the location for more realistic mapping
      double latOffset = (random.nextDouble() - 0.5) * 0.02; // +/- 0.01 degrees
      double longOffset = (random.nextDouble() - 0.5) * 0.02;
      item.setLatitude(location.getLatitude() + latOffset);
      item.setLongitude(location.getLongitude() + longOffset);
      
      item.setCondition(conditions[random.nextInt(conditions.length)]);
      item.setSize(sizes[random.nextInt(sizes.length)]);
      item.setBrand(brands[random.nextInt(brands.length)]);
      item.setColor(colors[random.nextInt(colors.length)]);
      item.setAvailable(true);
      item.setVippsPaymentEnabled(random.nextBoolean());
      
      LocalDateTime createdTime = LocalDateTime.now().minusDays(random.nextInt(60)); // Items created within last 60 days
      item.setCreatedAt(createdTime);
      item.setUpdatedAt(createdTime);
      
      Item savedItem = itemRepository.save(item);
      createdItems.add(savedItem);
      
      // Add 1-3 images to each item
      int imageCount = random.nextInt(3) + 1;
      for (int j = 0; j < imageCount; j++) {
        createItemImage(savedItem, imageUrls[random.nextInt(imageUrls.length)], j == 0, j);
      }
    }
    
    logger.info("Created {} items for user {}", createdItems.size(), seller.getUsername());
    return createdItems;
  }
  
  @Transactional
  protected void createItemImage(Item item, String imageUrl, boolean isPrimary, int displayOrder) {
    ItemImage image = new ItemImage();
    image.setItem(item);
    image.setImageUrl(imageUrl);
    image.setPrimary(isPrimary);
    image.setDisplayOrder(displayOrder);
    itemImageRepository.save(image);
  }
  
  @Transactional
  protected void createFavorites(User buyer, Map<User, List<Item>> userItems) {
    logger.info("Creating favorites for user {}", buyer.getUsername());
    Random random = new Random();
    int favCount = 0;
    
    for (Map.Entry<User, List<Item>> entry : userItems.entrySet()) {
      User seller = entry.getKey();
      List<Item> items = entry.getValue();
      
      // Buyer favorites some items from each seller (30% chance per item)
      for (Item item : items) {
        if (random.nextDouble() < 0.3) {
          Favorite favorite = new Favorite();
          favorite.setUser(buyer);
          favorite.setItem(item);
          favorite.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
          favorite.setActive(true);
          
          favoriteRepository.save(favorite);
          favCount++;
        }
      }
    }
    
    logger.info("Created {} favorites for user {}", favCount, buyer.getUsername());
  }
  
  @Transactional
  protected void createConversations(Map<User, List<Item>> userItems, List<User> sellers, User buyer) {
    logger.info("Creating conversations between sellers and buyer");
    Random random = new Random();
    
    // Create conversation starters and responses
    String[] inquiries = {
      "Hi! I'm interested in this item. Is it still available?",
      "Hello, do you offer any discount if I buy this item?",
      "Hi there! Could you provide more details about the size and fit?",
      "Is the color in the photos accurate? It looks a bit different on my screen.",
      "Hi, I'm wondering if you'd consider a trade for a similar item?",
      "Hello! Can I pick this up locally to avoid shipping costs?",
      "I really like this! Would you accept payment through Vipps?",
      "Hi, do you know how long shipping would take to Bergen?",
      "Hello! Is this item new or has it been worn before?",
      "I'm interested in buying this as a gift. Do you offer gift wrapping?"
    };
    
    String[] responses = {
      "Hi! Yes, it's still available. Let me know if you're interested in purchasing.",
      "Hello! Thanks for your interest. I might consider a small discount depending on the item.",
      "The item fits true to size. I'm normally a %s and it fits me perfectly.",
      "The color is actually very close to what's shown in the photos. It's a nice %s shade.",
      "I'm not really looking for trades at the moment, but thank you for the offer!",
      "Yes, local pickup is definitely an option! I'm located in %s area.",
      "Yes, I accept Vipps payments! It's actually my preferred payment method.",
      "Shipping to Bergen usually takes about 2-3 business days with the current provider.",
      "As mentioned in the description, the condition is %s. Please let me know if you have other questions!",
      "I don't offer gift wrapping, but the item will be carefully packaged for shipping."
    };
    
    String[] followups = {
      "Great! I'd like to buy it. How do we proceed?",
      "Perfect! Could we agree on %s kr? That would work great for my budget.",
      "Thanks for the info! I think it will fit well then. I'll take it!",
      "That sounds good. Could you hold it for me until tomorrow?",
      "I appreciate your quick response! I'll place the order now.",
      "Excellent! I can pick it up this weekend if that works for you?",
      "Great! I'll send payment right away via Vipps.",
      "That shipping time works for me. I'll proceed with the purchase.",
      "Thanks for confirming. I'm ready to move forward with buying it.",
      "Sounds good. Just sent you a payment request!"
    };
    
    // Create 5-10 conversations with different sellers about random items
    int conversationCount = random.nextInt(6) + 5;
    int createdCount = 0;
    
    for (int i = 0; i < conversationCount && i < sellers.size(); i++) {
      User seller = sellers.get(i);
      List<Item> sellerItems = userItems.get(seller);
      
      if (sellerItems != null && !sellerItems.isEmpty()) {
        // Pick a random item from this seller
        Item item = sellerItems.get(random.nextInt(sellerItems.size()));
        
        // Generate the conversation
        LocalDateTime startTime = LocalDateTime.now().minusDays(random.nextInt(20) + 1);
        
        // Buyer's initial message
        String inquiry = inquiries[random.nextInt(inquiries.length)];
        Message buyerMessage = new Message();
        buyerMessage.setSenderId(buyer.getId().toString());
        buyerMessage.setReceiverId(seller.getId().toString());
        buyerMessage.setContent(inquiry);
        buyerMessage.setCreatedAt(startTime);
        buyerMessage.setRead(true);
        buyerMessage.setItem(item);
        messageRepository.save(buyerMessage);
        
        // Seller's response (1-6 hours later)
        LocalDateTime responseTime = startTime.plusHours(random.nextInt(5) + 1);
        String response = responses[random.nextInt(responses.length)]
            .replace("%s", item.getSize())
            .replace("%s", item.getColor())
            .replace("%s", item.getLocation().getCity())
            .replace("%s", item.getCondition());
            
        Message sellerMessage = new Message();
        sellerMessage.setSenderId(seller.getId().toString());
        sellerMessage.setReceiverId(buyer.getId().toString());
        sellerMessage.setContent(response);
        sellerMessage.setCreatedAt(responseTime);
        sellerMessage.setRead(true);
        sellerMessage.setItem(item);
        messageRepository.save(sellerMessage);
        
        // Potential buyer followup (50% chance, 1-8 hours later)
        if (random.nextBoolean()) {
          LocalDateTime followupTime = responseTime.plusHours(random.nextInt(7) + 1);
          String followup = followups[random.nextInt(followups.length)]
              .replace("%s", String.valueOf((int)(item.getPrice() * 0.9))); // Suggest 10% discount
              
          Message buyerFollowup = new Message();
          buyerFollowup.setSenderId(buyer.getId().toString());
          buyerFollowup.setReceiverId(seller.getId().toString());
          buyerFollowup.setContent(followup);
          buyerFollowup.setCreatedAt(followupTime);
          buyerFollowup.setRead(random.nextBoolean()); // Some might be unread
          buyerFollowup.setItem(item);
          messageRepository.save(buyerFollowup);
        }
        
        createdCount++;
      }
    }
    
    logger.info("Created {} conversations", createdCount);
  }
  
  @Transactional
  protected void createTransactions(Map<User, List<Item>> userItems, User buyer) {
    logger.info("Creating transactions");
    Random random = new Random();
    
    String[] statuses = {"COMPLETED", "PENDING", "FAILED", "CANCELLED"};
    String[] paymentMethods = {"Vipps", "Credit Card", "Bank Transfer", "Cash on Delivery"};
    
    // Create 3-5 completed purchases for the buyer
    int transactionCount = random.nextInt(3) + 3;
    int createdCount = 0;
    
    for (Map.Entry<User, List<Item>> entry : userItems.entrySet()) {
      if (createdCount >= transactionCount) break;
      
      User seller = entry.getKey();
      List<Item> items = entry.getValue();
      
      if (!items.isEmpty()) {
        // Pick a random item from this seller
        Item item = items.get(random.nextInt(items.size()));
        
        // Create a transaction (usually completed, sometimes other statuses)
        Transaction transaction = new Transaction();
        transaction.setItem(item);
        transaction.setBuyerId(buyer.getId().toString());
        transaction.setSellerId(seller.getId().toString());
        
        // Price might be slightly different than list price (negotiation)
        double transactionAmount = item.getPrice() * (0.85 + random.nextDouble() * 0.15);
        transaction.setAmount(transactionAmount);
        
        // Make most transactions completed, few in other states
        double statusRoll = random.nextDouble();
        if (statusRoll < 0.8) {
          transaction.setStatus("COMPLETED");
          // Mark item as no longer available if transaction is completed
          item.setAvailable(false);
          itemRepository.save(item);
        } else {
          transaction.setStatus(statuses[random.nextInt(statuses.length)]);
        }
        
        transaction.setPaymentMethod(paymentMethods[random.nextInt(paymentMethods.length)]);
        
        // Transaction time in the past 30 days
        LocalDateTime transactionTime = LocalDateTime.now().minusDays(random.nextInt(30));
        transaction.setCreatedAt(transactionTime);
        transaction.setUpdatedAt(transactionTime.plusHours(random.nextInt(48))); // Updated within 48 hours
        
        transactionRepository.save(transaction);
        createdCount++;
      }
    }
    
    logger.info("Created {} transactions", createdCount);
  }
}