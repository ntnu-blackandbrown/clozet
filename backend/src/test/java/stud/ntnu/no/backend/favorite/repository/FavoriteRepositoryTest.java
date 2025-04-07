package stud.ntnu.no.backend.favorite.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.favorite.entity.Favorite;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(FavoriteRepositoryTestConfig.class)
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class FavoriteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Test
    public void testFindByUserId_WhenUserHasFavorites_ShouldReturnFavorites() {
        // Arrange
        User user = TestHelper.createUser(entityManager, "user1", "user1@example.com");
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        
        Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, category);
        entityManager.persist(item1);
        
        Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, category);
        entityManager.persist(item2);
        
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
        User user1 = TestHelper.createUser(entityManager, "user1", "user1@example.com");
        User user2 = TestHelper.createUser(entityManager, "user2", "user2@example.com");
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        
        Item item = TestHelper.createBasicItem(entityManager, "Test Item", seller, category);
        entityManager.persist(item);
        
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
        User user = TestHelper.createUser(entityManager, "user", "user@example.com");
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        
        Item item = TestHelper.createBasicItem(entityManager, "Test Item", seller, category);
        entityManager.persist(item);
        
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
        User user = TestHelper.createUser(entityManager, "user", "user@example.com");
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        
        Item item = TestHelper.createBasicItem(entityManager, "Test Item", seller, category);
        entityManager.persist(item);
        entityManager.flush();

        // Act
        boolean exists = favoriteRepository.existsByUserIdAndItemId(user.getId().toString(), item.getId());

        // Assert
        assertThat(exists).isFalse();
    }
} 