package stud.ntnu.no.backend.review.service;

import java.util.List;
import stud.ntnu.no.backend.review.dto.ReviewDTO;

/**
 * Service interface for managing reviews.
 *
 * <p>This interface defines methods for CRUD operations on reviews.
 */
public interface ReviewService {

  /**
   * Retrieves all reviews.
   *
   * @return a list of ReviewDTOs
   */
  List<ReviewDTO> getAllReviews();

  /**
   * Retrieves a review by its ID.
   *
   * @param id the ID of the review
   * @return the ReviewDTO
   */
  ReviewDTO getReview(Long id);

  /**
   * Retrieves reviews by the reviewee's ID.
   *
   * @param revieweeId the ID of the reviewee
   * @return a list of ReviewDTOs
   */
  List<ReviewDTO> getReviewsByReviewee(Long revieweeId);

  /**
   * Retrieves reviews by the reviewer's ID.
   *
   * @param reviewerId the ID of the reviewer
   * @return a list of ReviewDTOs
   */
  List<ReviewDTO> getReviewsByReviewer(Long reviewerId);

  /**
   * Creates a new review.
   *
   * @param reviewDTO the ReviewDTO
   * @return the created ReviewDTO
   */
  ReviewDTO createReview(ReviewDTO reviewDTO);

  /**
   * Updates an existing review.
   *
   * @param id the ID of the review to update
   * @param reviewDTO the ReviewDTO with updated information
   * @return the updated ReviewDTO
   */
  ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);

  /**
   * Deletes a review by its ID.
   *
   * @param id the ID of the review to delete
   */
  void deleteReview(Long id);
}
