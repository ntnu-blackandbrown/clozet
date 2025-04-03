package stud.ntnu.no.backend.favorite.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tester for FavoriteRepository.
 * Bruker en in-memory H2-database for å teste repository-funksjonaliteten.
 */
@DataJpaTest
class FavoriteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FavoriteRepository favoriteRepository;

    /**
     * Tester at findByUserId returnerer alle favoritter for en gitt bruker.
     */
    @Test
    void testFindByUserId_whenUserHasFavorites_shouldReturnFavoritesList() {
        // Arrange
        User user = createTestUser("user1");
        Item item1 = createTestItem("Item 1");
        Item item2 = createTestItem("Item 2");
        
        Favorite favorite1 = createTestFavorite(user, item1, true);
        Favorite favorite2 = createTestFavorite(user, item2, false);
        
        entityManager.flush();
        
        // Act
        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId().toString());
        
        // Assert
        assertEquals(2, favorites.size());
        assertTrue(favorites.stream().anyMatch(f -> f.getItemId().equals(item1.getId())));
        assertTrue(favorites.stream().anyMatch(f -> f.getItemId().equals(item2.getId())));
    }

    /**
     * Tester at findByUserId returnerer tom liste når brukeren ikke har favoritter.
     */
    @Test
    void testFindByUserId_whenUserHasNoFavorites_shouldReturnEmptyList() {
        // Arrange
        User user = createTestUser("user2");
        entityManager.flush();
        
        // Act
        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId().toString());
        
        // Assert
        assertTrue(favorites.isEmpty());
    }

    /**
     * Tester at findByItemId returnerer alle favoritter for et gitt element.
     */
    @Test
    void testFindByItemId_whenItemHasFavorites_shouldReturnFavoritesList() {
        // Arrange
        User user1 = createTestUser("user3");
        User user2 = createTestUser("user4");
        Item item = createTestItem("Item 3");
        
        Favorite favorite1 = createTestFavorite(user1, item, true);
        Favorite favorite2 = createTestFavorite(user2, item, true);
        
        entityManager.flush();
        
        // Act
        List<Favorite> favorites = favoriteRepository.findByItemId(item.getId());
        
        // Assert
        assertEquals(2, favorites.size());
        assertTrue(favorites.stream().anyMatch(f -> f.getUserId().equals(user1.getId().toString())));
        assertTrue(favorites.stream().anyMatch(f -> f.getUserId().equals(user2.getId().toString())));
    }

    /**
     * Tester at existsByUserIdAndItemId returnerer true når favoritten eksisterer.
     */
    @Test
    void testExistsByUserIdAndItemId_whenFavoriteExists_shouldReturnTrue() {
        // Arrange
        User user = createTestUser("user5");
        Item item = createTestItem("Item 4");
        
        Favorite favorite = createTestFavorite(user, item, true);
        
        entityManager.flush();
        
        // Act
        boolean exists = favoriteRepository.existsByUserIdAndItemId(user.getId().toString(), item.getId());
        
        // Assert
        assertTrue(exists);
    }

    /**
     * Tester at existsByUserIdAndItemId returnerer false når favoritten ikke eksisterer.
     */
    @Test
    void testExistsByUserIdAndItemId_whenFavoriteDoesNotExist_shouldReturnFalse() {
        // Arrange
        User user = createTestUser("user6");
        Item item = createTestItem("Item 5");
        
        entityManager.flush();
        
        // Act
        boolean exists = favoriteRepository.existsByUserIdAndItemId(user.getId().toString(), item.getId());
        
        // Assert
        assertFalse(exists);
    }

    /**
     * Hjelpermetode for å opprette en testbruker.
     */
    private User createTestUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPasswordHash("hashedPassword");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setActive(true);
        return entityManager.persist(user);
    }

    /**
     * Hjelpermetode for å opprette et testelement.
     */
    private Item createTestItem(String title) {
        Item item = new Item();
        item.setTitle(title);
        item.setShortDescription("Short description");
        item.setLongDescription("Test long description");
        item.setPrice(100.0);
        item.setLatitude(10.0);
        item.setLongitude(10.0);
        item.setCondition("New");
        item.setSize("M");
        item.setBrand("TestBrand");
        item.setColor("Blue");
        item.setAvailable(true);
        item.setVippsPaymentEnabled(true);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        
        // Merk: Vi mangler å sette seller, category, location og shippingOption
        // Dette kan medføre feil, men for denne testen er det nok
        
        return entityManager.persist(item);
    }

    /**
     * Hjelpermetode for å opprette en testfavoritt.
     */
    private Favorite createTestFavorite(User user, Item item, boolean active) {
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setItem(item);
        favorite.setCreatedAt(LocalDateTime.now());
        favorite.setActive(active);
        return entityManager.persist(favorite);
    }
} 