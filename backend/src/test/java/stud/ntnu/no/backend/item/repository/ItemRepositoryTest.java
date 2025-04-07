package stud.ntnu.no.backend.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testFindBySellerId_WhenSellerHasItems_ShouldReturnItems() {
        // Arrange
        User seller1 = new User();
        seller1.setUsername("seller1");
        seller1.setEmail("seller1@example.com");
        seller1.setPasswordHash("password");
        seller1.setActive(true);
        entityManager.persist(seller1);
        
        User seller2 = new User();
        seller2.setUsername("seller2");
        seller2.setEmail("seller2@example.com");
        seller2.setPasswordHash("password");
        seller2.setActive(true);
        entityManager.persist(seller2);
        
        Category category = new Category();
        category.setName("Test Category");
        entityManager.persist(category);
        
        Item item1 = createBasicItem("Item 1", seller1, category);
        Item item2 = createBasicItem("Item 2", seller1, category);
        Item item3 = createBasicItem("Item 3", seller2, category);
        
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
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
        User seller = new User();
        seller.setUsername("seller");
        seller.setEmail("seller@example.com");
        seller.setPasswordHash("password");
        seller.setActive(true);
        entityManager.persist(seller);
        
        Category category1 = new Category();
        category1.setName("Category 1");
        entityManager.persist(category1);
        
        Category category2 = new Category();
        category2.setName("Category 2");
        entityManager.persist(category2);
        
        Item item1 = createBasicItem("Item 1", seller, category1);
        Item item2 = createBasicItem("Item 2", seller, category1);
        Item item3 = createBasicItem("Item 3", seller, category2);
        
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
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
        User seller = new User();
        seller.setUsername("seller");
        seller.setEmail("seller@example.com");
        seller.setPasswordHash("password");
        seller.setActive(true);
        entityManager.persist(seller);
        
        Category category = new Category();
        category.setName("Test Category");
        entityManager.persist(category);
        
        Item item1 = createBasicItem("Item 1", seller, category);
        item1.setAvailable(true);
        
        Item item2 = createBasicItem("Item 2", seller, category);
        item2.setAvailable(true);
        
        Item item3 = createBasicItem("Item 3", seller, category);
        item3.setAvailable(false);
        
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
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
        User seller = new User();
        seller.setUsername("seller");
        seller.setEmail("seller@example.com");
        seller.setPasswordHash("password");
        seller.setActive(true);
        entityManager.persist(seller);
        
        Category category = new Category();
        category.setName("Test Category");
        entityManager.persist(category);
        
        Item item1 = createBasicItem("Leather Jacket", seller, category);
        Item item2 = createBasicItem("Denim Jacket", seller, category);
        Item item3 = createBasicItem("Wool Sweater", seller, category);
        
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
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
        User seller = new User();
        seller.setUsername("seller");
        seller.setEmail("seller@example.com");
        seller.setPasswordHash("password");
        seller.setActive(true);
        entityManager.persist(seller);
        
        Category category1 = new Category();
        category1.setName("Category 1");
        entityManager.persist(category1);
        
        Category category2 = new Category();
        category2.setName("Category 2");
        entityManager.persist(category2);
        
        Item item1 = createBasicItem("Item 1", seller, category1);
        item1.setAvailable(true);
        
        Item item2 = createBasicItem("Item 2", seller, category1);
        item2.setAvailable(false);
        
        Item item3 = createBasicItem("Item 3", seller, category2);
        item3.setAvailable(true);
        
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.flush();

        // Act
        List<Item> items = itemRepository.findByCategoryIdAndIsAvailableTrue(category1.getId());

        // Assert
        assertThat(items).hasSize(1);
        assertThat(items).extracting(Item::getTitle).containsExactly("Item 1");
    }
    
    // Helper method to create a basic Item with required fields
    private Item createBasicItem(String title, User seller, Category category) {
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
        return item;
    }
} 