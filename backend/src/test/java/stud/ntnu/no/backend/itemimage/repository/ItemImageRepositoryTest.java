package stud.ntnu.no.backend.itemimage.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
import stud.ntnu.no.backend.itemimage.entity.ItemImage;
import stud.ntnu.no.backend.user.entity.User;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ItemImageRepositoryTestConfig.class)
@TestPropertySource(
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
    })
public class ItemImageRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private ItemImageRepository itemImageRepository;

  @Test
  public void testFindByItemId_WhenItemHasImages_ShouldReturnImages() {
    // Arrange
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
    Category category = TestHelper.createCategory(entityManager, "Test Category");

    Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, category);
    Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, category);

    TestHelper.createItemImage(entityManager, item1, "http://example.com/image1.jpg", 0);
    TestHelper.createItemImage(entityManager, item1, "http://example.com/image2.jpg", 1);
    TestHelper.createItemImage(entityManager, item2, "http://example.com/image3.jpg", 0);

    entityManager.flush();

    // Act
    List<ItemImage> images = itemImageRepository.findByItemId(item1.getId());

    // Assert
    assertThat(images).hasSize(2);
    assertThat(images)
        .extracting(ItemImage::getImageUrl)
        .containsExactlyInAnyOrder(
            "http://example.com/image1.jpg", "http://example.com/image2.jpg");
  }

  @Test
  public void testFindByItemId_WhenItemHasNoImages_ShouldReturnEmptyList() {
    // Arrange
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
    Category category = TestHelper.createCategory(entityManager, "Test Category");

    Item item = TestHelper.createBasicItem(entityManager, "Item without images", seller, category);

    entityManager.flush();

    // Act
    List<ItemImage> images = itemImageRepository.findByItemId(item.getId());

    // Assert
    assertThat(images).isEmpty();
  }

  @Test
  public void testFindByItemIdOrderByDisplayOrder_ShouldReturnImagesInOrder() {
    // Arrange
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
    Category category = TestHelper.createCategory(entityManager, "Test Category");

    Item item =
        TestHelper.createBasicItem(entityManager, "Item with ordered images", seller, category);

    TestHelper.createItemImage(entityManager, item, "http://example.com/third.jpg", 2);
    TestHelper.createItemImage(entityManager, item, "http://example.com/first.jpg", 0);
    TestHelper.createItemImage(entityManager, item, "http://example.com/second.jpg", 1);

    entityManager.flush();

    // Act
    List<ItemImage> orderedImages =
        itemImageRepository.findByItemIdOrderByDisplayOrder(item.getId());

    // Assert
    assertThat(orderedImages).hasSize(3);
    assertThat(orderedImages)
        .extracting(ItemImage::getImageUrl)
        .containsExactly(
            "http://example.com/first.jpg",
            "http://example.com/second.jpg",
            "http://example.com/third.jpg");
  }
}
