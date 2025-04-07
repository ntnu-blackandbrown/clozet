package stud.ntnu.no.backend.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ItemRepositoryTestConfig.class)
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testFindBySellerId_WhenSellerHasItems_ShouldReturnItems() {
        // Arrange
        User seller1 = TestHelper.createUser(entityManager, "seller1", "seller1@example.com");
        User seller2 = TestHelper.createUser(entityManager, "seller2", "seller2@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        
        Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller1, category);
        Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller1, category);
        Item item3 = TestHelper.createBasicItem(entityManager, "Item 3", seller2, category);
        
        entityManager.flush();

        // Act
        List<Item> items = itemRepository.findBySellerId(seller1.getId());

        // Assert
        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getTitle).containsExactlyInAnyOrder("Item 1", "Item 2");
    }

    @Test
    public void testFindByCategoryId_WhenCategoryHasItems_ShouldReturnItems() {
        // Arrange
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category1 = TestHelper.createCategory(entityManager, "Category 1");
        Category category2 = TestHelper.createCategory(entityManager, "Category 2");
        
        Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, category1);
        Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, category1);
        Item item3 = TestHelper.createBasicItem(entityManager, "Item 3", seller, category2);
        
        entityManager.flush();

        // Act
        List<Item> items = itemRepository.findByCategoryId(category1.getId());

        // Assert
        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getTitle).containsExactlyInAnyOrder("Item 1", "Item 2");
    }

    @Test
    public void testFindByIsAvailableTrue_WhenItemsAreAvailable_ShouldReturnItems() {
        // Arrange
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        
        Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, category);
        item1.setAvailable(true);
        
        Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, category);
        item2.setAvailable(true);
        
        Item item3 = TestHelper.createBasicItem(entityManager, "Item 3", seller, category);
        item3.setAvailable(false);
        
        entityManager.flush();

        // Act
        List<Item> items = itemRepository.findByIsAvailableTrue();

        // Assert
        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getTitle).containsExactlyInAnyOrder("Item 1", "Item 2");
    }

    @Test
    public void testFindByTitleContainingIgnoreCase_WhenTitleMatches_ShouldReturnItems() {
        // Arrange
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category = TestHelper.createCategory(entityManager, "Test Category");
        
        Item item1 = TestHelper.createBasicItem(entityManager, "Leather Jacket", seller, category);
        Item item2 = TestHelper.createBasicItem(entityManager, "Denim Jacket", seller, category);
        Item item3 = TestHelper.createBasicItem(entityManager, "Wool Sweater", seller, category);
        
        entityManager.flush();

        // Act
        List<Item> items = itemRepository.findByTitleContainingIgnoreCase("jacket");

        // Assert
        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getTitle).containsExactlyInAnyOrder("Leather Jacket", "Denim Jacket");
    }

    @Test
    public void testFindByCategoryIdAndIsAvailableTrue_WhenBothConditionsMatch_ShouldReturnItems() {
        // Arrange
        User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
        
        Category category1 = TestHelper.createCategory(entityManager, "Category 1");
        Category category2 = TestHelper.createCategory(entityManager, "Category 2");
        
        Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, category1);
        item1.setAvailable(true);
        
        Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, category1);
        item2.setAvailable(false);
        
        Item item3 = TestHelper.createBasicItem(entityManager, "Item 3", seller, category2);
        item3.setAvailable(true);
        
        entityManager.flush();

        // Act
        List<Item> items = itemRepository.findByCategoryIdAndIsAvailableTrue(category1.getId());

        // Assert
        assertThat(items).hasSize(1);
        assertThat(items).extracting(Item::getTitle).containsExactly("Item 1");
    }
} 