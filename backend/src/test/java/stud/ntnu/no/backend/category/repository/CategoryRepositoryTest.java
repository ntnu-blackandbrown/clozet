package stud.ntnu.no.backend.category.repository;

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
@Import(CategoryRepositoryTestConfig.class)
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testFindByParentId_WhenParentHasSubcategories_ShouldReturnSubcategories() {
        // Arrange
        Category parent = new Category();
        parent.setName("Parent Category");
        parent.setDescription("Parent Category Description");
        entityManager.persist(parent);

        Category child1 = new Category();
        child1.setName("Child Category 1");
        child1.setDescription("Child Category 1 Description");
        child1.setParent(parent);
        entityManager.persist(child1);

        Category child2 = new Category();
        child2.setName("Child Category 2");
        child2.setDescription("Child Category 2 Description");
        child2.setParent(parent);
        entityManager.persist(child2);

        Category otherCategory = new Category();
        otherCategory.setName("Other Category");
        otherCategory.setDescription("Other Category Description");
        entityManager.persist(otherCategory);

        entityManager.flush();

        // Act
        List<Category> subcategories = categoryRepository.findByParentId(parent.getId());

        // Assert
        assertThat(subcategories).hasSize(2);
        assertThat(subcategories).extracting(Category::getName)
                .containsExactlyInAnyOrder("Child Category 1", "Child Category 2");
    }

    @Test
    public void testExistsByName_WhenNameExists_ShouldReturnTrue() {
        // Arrange
        Category category = new Category();
        category.setName("Unique Category");
        category.setDescription("Unique Category Description");
        entityManager.persist(category);
        entityManager.flush();

        // Act
        boolean exists = categoryRepository.existsByName("Unique Category");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    public void testExistsByName_WhenNameDoesNotExist_ShouldReturnFalse() {
        // Act
        boolean exists = categoryRepository.existsByName("Nonexistent Category");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    public void testFindTopCategoriesByFavoriteCount_ShouldReturnCategoriesOrderedByFavoriteCount() {
        // This test is more complex and requires setting up related entities
        
        // Arrange - Create users
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPasswordHash("password");
        user1.setActive(true);
        entityManager.persist(user1);
        
        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPasswordHash("password");
        user2.setActive(true);
        entityManager.persist(user2);
        
        User seller = new User();
        seller.setUsername("seller");
        seller.setEmail("seller@example.com");
        seller.setPasswordHash("password");
        seller.setActive(true);
        entityManager.persist(seller);
        
        // Create categories
        Category popularCategory = new Category();
        popularCategory.setName("Popular Category");
        popularCategory.setDescription("Popular Category Description");
        entityManager.persist(popularCategory);
        
        Category lessPopularCategory = new Category();
        lessPopularCategory.setName("Less Popular Category");
        lessPopularCategory.setDescription("Less Popular Category Description");
        entityManager.persist(lessPopularCategory);
        
        // Create items in categories - using the helper to handle Location and ShippingOption
        Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, popularCategory);
        entityManager.persist(item1);
        
        Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, popularCategory);
        entityManager.persist(item2);
        
        Item item3 = TestHelper.createBasicItem(entityManager, "Item 3", seller, lessPopularCategory);
        entityManager.persist(item3);
        
        // Create favorites
        Favorite favorite1 = new Favorite();
        favorite1.setUser(user1);
        favorite1.setItem(item1);
        favorite1.setCreatedAt(LocalDateTime.now());
        entityManager.persist(favorite1);
        
        Favorite favorite2 = new Favorite();
        favorite2.setUser(user2);
        favorite2.setItem(item1);
        favorite2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(favorite2);
        
        Favorite favorite3 = new Favorite();
        favorite3.setUser(user1);
        favorite3.setItem(item2);
        favorite3.setCreatedAt(LocalDateTime.now());
        entityManager.persist(favorite3);
        
        Favorite favorite4 = new Favorite();
        favorite4.setUser(user2);
        favorite4.setItem(item3);
        favorite4.setCreatedAt(LocalDateTime.now());
        entityManager.persist(favorite4);
        
        entityManager.flush();
        
        // Act
        List<Category> topCategories = categoryRepository.findTopCategoriesByFavoriteCount(org.springframework.data.domain.PageRequest.of(0, 2));
        
        // Assert
        assertThat(topCategories).hasSize(2);
        assertThat(topCategories.get(0).getName()).isEqualTo("Popular Category");
        assertThat(topCategories.get(1).getName()).isEqualTo("Less Popular Category");
    }
} 