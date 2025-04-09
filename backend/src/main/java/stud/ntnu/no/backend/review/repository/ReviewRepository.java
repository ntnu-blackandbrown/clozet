package stud.ntnu.no.backend.review.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stud.ntnu.no.backend.review.entity.Review;

/**
 * Repository interface for Review entities.
 *
 * <p>This interface extends JpaRepository to provide CRUD operations for Review entities.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

  /**
   * Finds reviews by reviewee ID.
   *
   * @param revieweeId the reviewee ID
   * @return a list of reviews
   */
  List<Review> findByRevieweeId(Long revieweeId);

  /**
   * Finds reviews by reviewer ID.
   *
   * @param reviewerId the reviewer ID
   * @return a list of reviews
   */
  List<Review> findByReviewerId(Long reviewerId);

  /**
   * Finds reviews by transaction ID.
   *
   * @param transactionId the transaction ID
   * @return a list of reviews
   */
  List<Review> findByTransactionId(Long transactionId);
}
