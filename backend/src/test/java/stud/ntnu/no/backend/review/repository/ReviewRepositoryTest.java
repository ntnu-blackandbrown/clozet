package stud.ntnu.no.backend.review.repository;

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
import stud.ntnu.no.backend.review.entity.Review;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.user.entity.User;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ReviewRepositoryTestConfig.class)
@TestPropertySource(
    properties = {
      "spring.main.allow-bean-definition-overriding=true",
      "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
    })
public class ReviewRepositoryTest {

  @Autowired private TestEntityManager entityManager;

  @Autowired private ReviewRepository reviewRepository;

  @Test
  public void testFindByRevieweeId_WhenReviewsExist_ShouldReturnReviews() {
    // Arrange
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
    User buyer1 = TestHelper.createUser(entityManager, "buyer1", "buyer1@example.com");
    User buyer2 = TestHelper.createUser(entityManager, "buyer2", "buyer2@example.com");

    Category category = TestHelper.createCategory(entityManager, "Test Category");
    Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller, category);
    Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller, category);

    Transaction transaction1 =
        TestHelper.createTransaction(entityManager, buyer1, seller, item1, 100.0);
    Transaction transaction2 =
        TestHelper.createTransaction(entityManager, buyer2, seller, item2, 200.0);

    TestHelper.createReview(entityManager, buyer1, seller, transaction1, 5, "Great seller!");
    TestHelper.createReview(entityManager, buyer2, seller, transaction2, 4, "Good experience");

    entityManager.flush();

    // Act
    List<Review> reviews = reviewRepository.findByRevieweeId(seller.getId());

    // Assert
    assertThat(reviews).hasSize(2);
    assertThat(reviews)
        .extracting(r -> r.getReviewer().getUsername())
        .containsExactlyInAnyOrder("buyer1", "buyer2");
    assertThat(reviews).extracting(Review::getRating).containsExactlyInAnyOrder(5, 4);
  }

  @Test
  public void testFindByReviewerId_WhenReviewsExist_ShouldReturnReviews() {
    // Arrange
    User buyer = TestHelper.createUser(entityManager, "buyer", "buyer@example.com");
    User seller1 = TestHelper.createUser(entityManager, "seller1", "seller1@example.com");
    User seller2 = TestHelper.createUser(entityManager, "seller2", "seller2@example.com");

    Category category = TestHelper.createCategory(entityManager, "Test Category");
    Item item1 = TestHelper.createBasicItem(entityManager, "Item 1", seller1, category);
    Item item2 = TestHelper.createBasicItem(entityManager, "Item 2", seller2, category);

    Transaction transaction1 =
        TestHelper.createTransaction(entityManager, buyer, seller1, item1, 100.0);
    Transaction transaction2 =
        TestHelper.createTransaction(entityManager, buyer, seller2, item2, 200.0);

    TestHelper.createReview(entityManager, buyer, seller1, transaction1, 5, "Great seller!");
    TestHelper.createReview(entityManager, buyer, seller2, transaction2, 3, "Okay experience");

    entityManager.flush();

    // Act
    List<Review> reviews = reviewRepository.findByReviewerId(buyer.getId());

    // Assert
    assertThat(reviews).hasSize(2);
    assertThat(reviews)
        .extracting(r -> r.getReviewee().getUsername())
        .containsExactlyInAnyOrder("seller1", "seller2");
    assertThat(reviews).extracting(Review::getRating).containsExactlyInAnyOrder(5, 3);
  }

  @Test
  public void testFindByTransactionId_WhenReviewExists_ShouldReturnReview() {
    // Arrange
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
    User buyer = TestHelper.createUser(entityManager, "buyer", "buyer@example.com");

    Category category = TestHelper.createCategory(entityManager, "Test Category");
    Item item = TestHelper.createBasicItem(entityManager, "Test Item", seller, category);

    Transaction transaction =
        TestHelper.createTransaction(entityManager, buyer, seller, item, 150.0);

    Review review =
        TestHelper.createReview(entityManager, buyer, seller, transaction, 4, "Good seller");

    entityManager.flush();

    // Act
    List<Review> reviews = reviewRepository.findByTransactionId(transaction.getId());

    // Assert
    assertThat(reviews).hasSize(1);
    assertThat(reviews.get(0).getComment()).isEqualTo("Good seller");
    assertThat(reviews.get(0).getRating()).isEqualTo(4);
  }

  @Test
  public void testFindByTransactionId_WhenNoReviewExists_ShouldReturnEmptyList() {
    // Arrange
    User seller = TestHelper.createUser(entityManager, "seller", "seller@example.com");
    User buyer = TestHelper.createUser(entityManager, "buyer", "buyer@example.com");

    Category category = TestHelper.createCategory(entityManager, "Test Category");
    Item item = TestHelper.createBasicItem(entityManager, "Test Item", seller, category);

    Transaction transaction =
        TestHelper.createTransaction(entityManager, buyer, seller, item, 150.0);

    entityManager.flush();

    // Act
    List<Review> reviews = reviewRepository.findByTransactionId(transaction.getId());

    // Assert
    assertThat(reviews).isEmpty();
  }
}
