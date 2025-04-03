package stud.ntnu.no.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.category.repository.CategoryRepository;
import stud.ntnu.no.backend.location.entity.Location;
import stud.ntnu.no.backend.location.repository.LocationRepository;
import stud.ntnu.no.backend.shippingoption.entity.ShippingOption;
import stud.ntnu.no.backend.shippingoption.repository.ShippingOptionRepository;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.item.repository.ItemRepository;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.favorite.repository.FavoriteRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@TestConfiguration
@ActiveProfiles("test")
public class TestDatabaseInitializer {

    @Bean
    CommandLineRunner testInitDatabase(UserRepository userRepository,
                                       CategoryRepository categoryRepository,
                                       ItemRepository itemRepository,
                                       FavoriteRepository favoriteRepository,
                                       LocationRepository locationRepository,
                                       ShippingOptionRepository shippingOptionRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            // Clear existing data
            // Ensure the deletion order respects foreign key constraints
            favoriteRepository.deleteAll();
            itemRepository.deleteAll();
            categoryRepository.deleteAll();
            locationRepository.deleteAll();
            shippingOptionRepository.deleteAll();
            userRepository.deleteAll();

            // Create admin user
            User admin = new User();
            admin.setUsername("Admin");
            admin.setPasswordHash(passwordEncoder.encode("Admin1234"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("Administrator");
            admin.setRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);

            // Create regular user for favorites
            User user = new User();
            user.setUsername("TestUser");
            user.setPasswordHash(passwordEncoder.encode("Test1234"));
            user.setEmail("test@example.com");
            user.setFirstName("Test");
            user.setLastName("User");
            user.setRole("USER");
            user.setActive(true);
            userRepository.save(user);

            // Create a location
            Location location = new Location();
            location.setLatitude(50.45);
            location.setLongitude(50.45);
            location.setRegion("North America");
            location.setCity("New York");
            // Save location to database before using it
            locationRepository.save(location);

            // Create a shipping option
            ShippingOption shippingOption = new ShippingOption();
            shippingOption.setName("Test Shipping");
            shippingOption.setDescription("Test Description");
            shippingOption.setPrice(100.0);
            shippingOption.setEstimatedDays(3);
            shippingOption.setTracked(true);
            shippingOptionRepository.save(shippingOption);

            // Create categories
            List<Category> categories = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                Category category = new Category();
                category.setName("Category " + i);
                category.setDescription("Description " + i);
                category.setCreatedAt(LocalDateTime.now());
                category.setUpdatedAt(LocalDateTime.now());
                categories.add(categoryRepository.save(category));
            }

            // Create items for each category
            for (Category category : categories) {
                for (int i = 1; i <= 3; i++) {
                    Item item = new Item();
                    item.setSeller(user);
                    item.setTitle("Item " + i + " in " + category.getName());
                    item.setShortDescription("Short Description");
                    item.setLongDescription("Long Description");
                    item.setCategory(category);
                    item.setLocation(location);
                    item.setShippingOption(shippingOption);
                    item.setPrice(99.99);
                    item.setLatitude(59.9139);
                    item.setLongitude(10.7522);
                    item.setCondition("New");
                    item.setSize("Medium");
                    item.setBrand("TestBrand");
                    item.setColor("Blue");
                    item.setAvailable(true);
                    item.setVippsPaymentEnabled(true);
                    item.setCreatedAt(LocalDateTime.now());
                    item.setUpdatedAt(LocalDateTime.now());
                    itemRepository.save(item);

                    // Create favorites with different counts to establish ranking
                    if (category.getName().equals("Category 1")) {
                        createFavorites(favoriteRepository, item, user, 10);
                    } else if (category.getName().equals("Category 2")) {
                        createFavorites(favoriteRepository, item, user, 8);
                    } else if (category.getName().equals("Category 3")) {
                        createFavorites(favoriteRepository, item, user, 6);
                    } else if (category.getName().equals("Category 4")) {
                        createFavorites(favoriteRepository, item, user, 4);
                    } else if (category.getName().equals("Category 5")) {
                        createFavorites(favoriteRepository, item, user, 2);
                    }
                }
            }
        };
    }

    private void createFavorites(FavoriteRepository favoriteRepository, Item item, User user, int count) {
        for (int i = 0; i < count; i++) {
            Favorite favorite = new Favorite();
            favorite.setItem(item);
            favorite.setUser(user);
            favorite.setCreatedAt(LocalDateTime.now());
            favoriteRepository.save(favorite);
        }
    }
}