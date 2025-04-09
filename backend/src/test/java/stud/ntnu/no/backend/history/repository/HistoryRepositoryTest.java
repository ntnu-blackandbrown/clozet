package stud.ntnu.no.backend.history.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import stud.ntnu.no.backend.category.entity.Category;
import stud.ntnu.no.backend.history.entity.History;
import stud.ntnu.no.backend.item.entity.Item;
import stud.ntnu.no.backend.user.entity.User;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(HistoryRepositoryTestConfig.class)
@TestPropertySource(
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
    })
public class HistoryRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private HistoryRepository historyRepository;

  @Test
  public void testFindByUserOrderByViewedAtDesc_ShouldReturnHistoryInDescendingOrder() {
    // Arrange
    User user = TestHelper.createUser(entityManager, "user", "user@example.com");
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");

    Category category = TestHelper.createCategory(entityManager, "Test Category");
    Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, category);
    Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, category);
    Item item3 = TestHelper.createBasicItem(entityManager, "Item 3", seller, category);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime oneHourAgo = now.minusHours(1);
    LocalDateTime twoHoursAgo = now.minusHours(2);

    TestHelper.createHistory(entityManager, user, item1, twoHoursAgo, true);
    TestHelper.createHistory(entityManager, user, item2, now, true);
    TestHelper.createHistory(entityManager, user, item3, oneHourAgo, true);

    entityManager.flush();

    // Act
    List<History> historyList = historyRepository.findByUserOrderByViewedAtDesc(user);

    // Assert
    assertThat(historyList).hasSize(3);
    assertThat(historyList.get(0).getItem().getTitle()).isEqualTo("Item 2");
    assertThat(historyList.get(1).getItem().getTitle()).isEqualTo("Item 3");
    assertThat(historyList.get(2).getItem().getTitle()).isEqualTo("Item 1");
  }

  @Test
  public void
      testFindByUserAndActiveOrderByViewedAtDesc_WhenActive_ShouldReturnOnlyActiveHistory() {
    // Arrange
    User user = TestHelper.createUser(entityManager, "user", "user@example.com");
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");

    Category category = TestHelper.createCategory(entityManager, "Test Category");
    Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, category);
    Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, category);
    Item item3 = TestHelper.createBasicItem(entityManager, "Item 3", seller, category);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime oneHourAgo = now.minusHours(1);
    LocalDateTime twoHoursAgo = now.minusHours(2);

    TestHelper.createHistory(entityManager, user, item1, twoHoursAgo, true);
    TestHelper.createHistory(entityManager, user, item2, now, false);
    TestHelper.createHistory(entityManager, user, item3, oneHourAgo, true);

    entityManager.flush();

    // Act
    List<History> activeHistory =
        historyRepository.findByUserAndActiveOrderByViewedAtDesc(user, true);

    // Assert
    assertThat(activeHistory).hasSize(2);
    assertThat(activeHistory.get(0).getItem().getTitle()).isEqualTo("Item 3");
    assertThat(activeHistory.get(1).getItem().getTitle()).isEqualTo("Item 1");
  }

  @Test
  public void testFindByUserAndItem_WhenHistoryExists_ShouldReturnHistory() {
    // Arrange
    User user = TestHelper.createUser(entityManager, "user", "user@example.com");
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");

    Category category = TestHelper.createCategory(entityManager, "Test Category");
    Item item = TestHelper.createBasicItem(entityManager, "Test Item", seller, category);

    History history =
        TestHelper.createHistory(entityManager, user, item, LocalDateTime.now(), true);

    entityManager.flush();

    // Act
    Optional<History> found = historyRepository.findByUserAndItem(user, item);

    // Assert
    assertThat(found).isPresent();
    assertThat(found.get().getUser().getUsername()).isEqualTo("user");
    assertThat(found.get().getItem().getTitle()).isEqualTo("Test Item");
  }

  @Test
  public void testFindByUserAndItem_WhenHistoryDoesNotExist_ShouldReturnEmpty() {
    // Arrange
    User user = TestHelper.createUser(entityManager, "user", "user@example.com");
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");

    Category category = TestHelper.createCategory(entityManager, "Test Category");
    Item item = TestHelper.createBasicItem(entityManager, "Test Item", seller, category);

    entityManager.flush();

    // Act
    Optional<History> found = historyRepository.findByUserAndItem(user, item);

    // Assert
    assertThat(found).isEmpty();
  }

  @Test
  public void testDeleteByUser_ShouldRemoveAllUserHistory() {
    // Arrange
    User user1 = TestHelper.createUser(entityManager, "user1", "user1@example.com");
    User user2 = TestHelper.createUser(entityManager, "user2", "user2@example.com");
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");

    Category category = TestHelper.createCategory(entityManager, "Test Category");
    Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, category);
    Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, category);
    Item item3 = TestHelper.createBasicItem(entityManager, "Item 3", seller, category);

    TestHelper.createHistory(entityManager, user1, item1, LocalDateTime.now(), true);
    TestHelper.createHistory(entityManager, user1, item2, LocalDateTime.now(), true);
    TestHelper.createHistory(entityManager, user2, item3, LocalDateTime.now(), true);

    entityManager.flush();

    // Verify initial state
    assertThat(historyRepository.findByUserOrderByViewedAtDesc(user1)).hasSize(2);
    assertThat(historyRepository.findByUserOrderByViewedAtDesc(user2)).hasSize(1);

    // Act
    historyRepository.deleteByUser(user1);
    entityManager.flush();
    entityManager.clear();

    // Assert
    assertThat(historyRepository.findByUserOrderByViewedAtDesc(user1)).isEmpty();
    assertThat(historyRepository.findByUserOrderByViewedAtDesc(user2)).hasSize(1);
  }
}
