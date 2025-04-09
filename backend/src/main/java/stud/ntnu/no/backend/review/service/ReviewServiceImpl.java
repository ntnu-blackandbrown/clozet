package stud.ntnu.no.backend.review.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.review.dto.ReviewDTO;
import stud.ntnu.no.backend.review.entity.Review;
import stud.ntnu.no.backend.review.exception.ReviewNotFoundException;
import stud.ntnu.no.backend.review.exception.ReviewValidationException;
import stud.ntnu.no.backend.review.mapper.ReviewMapper;
import stud.ntnu.no.backend.review.repository.ReviewRepository;

/**
 * Implementation of the ReviewService interface.
 * <p>
 * This class provides methods for managing reviews, including CRUD operations.
 */
@Service
public class ReviewServiceImpl implements ReviewService {

  private static final Logger logger = Logger.getLogger(ReviewServiceImpl.class.getName());

  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;

  /**
   * Constructs a new ReviewServiceImpl with the specified dependencies.
   *
   * @param reviewRepository the ReviewRepository
   * @param reviewMapper     the ReviewMapper
   */
  public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
    this.reviewRepository = reviewRepository;
    this.reviewMapper = reviewMapper;
  }

  @Override
  public List<ReviewDTO> getAllReviews() {
    logger.info("Fetching all reviews");
    return reviewMapper.toDtoList(reviewRepository.findAll());
  }

  @Override
  public ReviewDTO getReview(Long id) {
    logger.info("Fetching review with id: " + id);
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
    return reviewMapper.toDto(review);
  }

  @Override
  public List<ReviewDTO> getReviewsByReviewee(Long revieweeId) {
    logger.info("Fetching reviews for reviewee with id: " + revieweeId);
    return reviewMapper.toDtoList(reviewRepository.findByRevieweeId(revieweeId));
  }

  @Override
  public List<ReviewDTO> getReviewsByReviewer(Long reviewerId) {
    logger.info("Fetching reviews for reviewer with id: " + reviewerId);
    return reviewMapper.toDtoList(reviewRepository.findByReviewerId(reviewerId));
  }

  @Override
  @Transactional
  public ReviewDTO createReview(ReviewDTO reviewDTO) {
    logger.info("Creating new review");
    validateReview(reviewDTO);
    Review review = reviewMapper.toEntity(reviewDTO);
    review.setCreatedAt(LocalDateTime.now());
    Review savedReview = reviewRepository.save(review);
    return reviewMapper.toDto(savedReview);
  }

  @Override
  @Transactional
  public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
    logger.info("Updating review with id: " + id);
    Review existingReview = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

    validateUpdateReview(reviewDTO);

    if (reviewDTO.getComment() != null) {
      existingReview.setComment(reviewDTO.getComment());
    }
    if (reviewDTO.getRating() != null) {
      existingReview.setRating(reviewDTO.getRating());
    }

    Review updatedReview = reviewRepository.save(existingReview);
    return reviewMapper.toDto(updatedReview);
  }

  @Override
  @Transactional
  public void deleteReview(Long id) {
    logger.info("Deleting review with id: " + id);
    if (!reviewRepository.existsById(id)) {
      throw new ReviewNotFoundException("Review not found with id: " + id);
    }
    reviewRepository.deleteById(id);
  }

  /**
   * Validates the given ReviewDTO.
   * <p>
   * This method checks that the rating is not null and is between 1 and 5, and that the reviewer
   * and reviewee IDs are not null and not the same.
   *
   * @param reviewDTO the ReviewDTO to validate
   * @throws ReviewValidationException if validation fails
   */
  private void validateReview(ReviewDTO reviewDTO) {
    if (reviewDTO.getRating() == null) {
      throw new ReviewValidationException("Rating cannot be null");
    }

    if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
      throw new ReviewValidationException("Rating must be between 1 and 5");
    }

    if (reviewDTO.getReviewerId() == null) {
      throw new ReviewValidationException("Reviewer ID cannot be null");
    }

    if (reviewDTO.getRevieweeId() == null) {
      throw new ReviewValidationException("Reviewee ID cannot be null");
    }

    if (reviewDTO.getReviewerId().equals(reviewDTO.getRevieweeId())) {
      throw new ReviewValidationException("Users cannot review themselves");
    }
  }

  /**
   * Validates the given ReviewDTO for update.
   * <p>
   * This method checks that the rating, if provided, is between 1 and 5.
   *
   * @param reviewDTO the ReviewDTO to validate
   * @throws ReviewValidationException if validation fails
   */
  private void validateUpdateReview(ReviewDTO reviewDTO) {
    if (reviewDTO.getRating() != null && (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5)) {
      throw new ReviewValidationException("Rating must be between 1 and 5");
    }
  }
}
