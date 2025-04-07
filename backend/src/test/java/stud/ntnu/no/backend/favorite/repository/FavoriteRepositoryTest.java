package stud.ntnu.no.backend.favorite.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FavoriteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Test
    public void testFindByUserId_WhenUserHasFavorites_ShouldReturnFavorites() {
        // Arrange
        User user = createUser("user1", "user1@example.com");
        User seller = createUser("seller", "seller@example.com");
        
        Category category = new Category();
        category.setName("Test Category");
        entityManager.persist(category);
        
        Item item1 = createItem("Item 1", seller, category);
        Item item2 = createItem("Item 2", seller, category);
        
        Favorite favorite1 = new Favorite();
        favorite1.setUser(user);
        favorite1.setItem(item1);
        favorite1.setCreatedAt(LocalDateTime.now());
        
        Favorite favorite2 = new Favorite();
        favorite2.setUser(user);
        favorite2.setItem(item2);
        favorite2.setCreatedAt(LocalDateTime.now());
        
        entityManager.persist(favorite1);
        entityManager.persist(favorite2);
        entityManager.flush();

        // Act
        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId().toString());

        // Assert
        assertThat(favorites).hasSize(2);
        assertThat(favorites).extracting(f -> f.getItem().getTitle()).containsExactlyInAnyOrder("Item 1", "Item 2");
    }

    @Test
    public void testFindByItemId_WhenItemHasFavorites_ShouldReturnFavorites() {
        // Arrange
        User user1 = createUser("user1", "user1@example.com");
        User user2 = createUser("user2", "user2@example.com");
        User seller = createUser("seller", "seller@example.com");
        
        Category category = new Category();
        category.setName("Test Category");
        entityManager.persist(category);
        
        Item item = createItem("Test Item", seller, category);
        
        Favorite favorite1 = new Favorite();
        favorite1.setUser(user1);
        favorite1.setItem(item);
        favorite1.setCreatedAt(LocalDateTime.now());
        
        Favorite favorite2 = new Favorite();
        favorite2.setUser(user2);
        favorite2.setItem(item);
        favorite2.setCreatedAt(LocalDateTime.now());
        
        entityManager.persist(favorite1);
        entityManager.persist(favorite2);
        entityManager.flush();

        // Act
        List<Favorite> favorites = favoriteRepository.findByItemId(item.getId());

        // Assert
        assertThat(favorites).hasSize(2);
        assertThat(favorites).extracting(f -> f.getUser().getUsername()).containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    public void testExistsByUserIdAndItemId_WhenFavoriteExists_ShouldReturnTrue() {
        // Arrange
        User user = createUser("user", "user@example.com");
        User seller = createUser("seller", "seller@example.com");
        
        Category category = new Category();
        category.setName("Test Category");
        entityManager.persist(category);
        
        Item item = createItem("Test Item", seller, category);
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItem(item);
        favorite.setCreatedAt(LocalDateTime.now());
        
        entityManager.persist(favorite);
        entityManager.flush();

        // Act
        boolean exists = favoriteRepository.existsByUserIdAndItemId(user.getId().toString(), item.getId());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    public void testExistsByUserIdAndItemId_WhenFavoriteDoesNotExist_ShouldReturnFalse() {
        // Arrange
        User user = createUser("user", "user@example.com");
        User seller = createUser("seller", "seller@example.com");
        
        Category category = new Category();
        category.setName("Test Category");
        entityManager.persist(category);
        
        Item item = createItem("Test Item", seller, category);
        entityManager.persist(item);
        entityManager.flush();

        // Act
        boolean exists = favoriteRepository.existsByUserIdAndItemId(user.getId().toString(), item.getId());

        // Assert
        assertThat(exists).isFalse();
    }
    
    // Helper methods
    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("password");
        user.setActive(true);
        entityManager.persist(user);
        return user;
    }
    
    private Item createItem(String title, User seller, Category category) {
        Item item = new Item();
        item.setTitle(title);
        item.setSeller(seller);
        item.setCategory(category);
        item.setShortDescription("Short description");
        item.setLongDescription("Long description");
        item.setPrice(100.0);
        item.setCondition("New");
        item.setSize("M");
        item.setBrand("Brand");
        item.setColor("Black");
        item.setLatitude(0.0);
        item.setLongitude(0.0);
        item.setAvailable(true);
        item.setVippsPaymentEnabled(true);
        entityManager.persist(item);
        return item;
    }
} 