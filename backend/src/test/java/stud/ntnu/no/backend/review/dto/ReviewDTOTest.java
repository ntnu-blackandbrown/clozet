package stud.ntnu.no.backend.review.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReviewDTOTest {

  @Test
  void testGettersAndSetters() {
    ReviewDTO dto = new ReviewDTO();

    Long id = 1L;
    String comment = "Great seller, fast shipping!";
    Integer rating = 5;
    LocalDateTime createdAt = LocalDateTime.now();
    Long revieweeId = 2L;
    String revieweeUsername = "user2";
    Long reviewerId = 3L;
    String reviewerUsername = "user3";
    Long transactionId = 4L;

    dto.setId(id);
    dto.setComment(comment);
    dto.setRating(rating);
    dto.setCreatedAt(createdAt);
    dto.setRevieweeId(revieweeId);
    dto.setRevieweeUsername(revieweeUsername);
    dto.setReviewerId(reviewerId);
    dto.setReviewerUsername(reviewerUsername);
    dto.setTransactionId(transactionId);

    assertEquals(id, dto.getId());
    assertEquals(comment, dto.getComment());
    assertEquals(rating, dto.getRating());
    assertEquals(createdAt, dto.getCreatedAt());
    assertEquals(revieweeId, dto.getRevieweeId());
    assertEquals(revieweeUsername, dto.getRevieweeUsername());
    assertEquals(reviewerId, dto.getReviewerId());
    assertEquals(reviewerUsername, dto.getReviewerUsername());
    assertEquals(transactionId, dto.getTransactionId());
  }

  @Test
  void testDefaultValues() {
    ReviewDTO dto = new ReviewDTO();

    assertNull(dto.getId());
    assertNull(dto.getComment());
    assertNull(dto.getRating());
    assertNull(dto.getCreatedAt());
    assertNull(dto.getRevieweeId());
    assertNull(dto.getRevieweeUsername());
    assertNull(dto.getReviewerId());
    assertNull(dto.getReviewerUsername());
    assertNull(dto.getTransactionId());
  }

  @Test
  void testValidationAnnotations() {
    // This test is meant to verify the validation annotations work as expected
    // In a full test setup, you would use a Validator to check these constraints
    ReviewDTO dto = new ReviewDTO();

    // @NotNull for rating
    assertNull(dto.getRating());

    // Valid rating (1-5)
    dto.setRating(3);
    assertEquals(3, dto.getRating());

    // If we had a validator, we would test:
    // - Rating cannot be null
    // - Rating cannot be less than 1
    // - Rating cannot be more than 5
  }
}
