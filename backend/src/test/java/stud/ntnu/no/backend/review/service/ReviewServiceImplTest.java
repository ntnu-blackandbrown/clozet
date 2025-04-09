package stud.ntnu.no.backend.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stud.ntnu.no.backend.review.dto.ReviewDTO;
import stud.ntnu.no.backend.review.entity.Review;
import stud.ntnu.no.backend.review.exception.ReviewNotFoundException;
import stud.ntnu.no.backend.review.exception.ReviewValidationException;
import stud.ntnu.no.backend.review.mapper.ReviewMapper;
import stud.ntnu.no.backend.review.repository.ReviewRepository;
import stud.ntnu.no.backend.user.entity.User;

class ReviewServiceImplTest {

  @Mock private ReviewRepository reviewRepository;

  @Mock private ReviewMapper reviewMapper;

  @InjectMocks private ReviewServiceImpl reviewService;

  private Review review;
  private ReviewDTO reviewDTO;
  private List<Review> reviewList;
  private List<ReviewDTO> reviewDTOList;
  private User reviewer;
  private User reviewee;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Setup users
    reviewer = new User();
    reviewer.setId(1L);
    reviewer.setUsername("reviewer");

    reviewee = new User();
    reviewee.setId(2L);
    reviewee.setUsername("reviewee");

    // Setup test data
    review = new Review();
    review.setId(1L);
    review.setReviewer(reviewer);
    review.setReviewee(reviewee);
    review.setRating(5);
    review.setComment("Great service!");
    review.setCreatedAt(LocalDateTime.now());

    reviewDTO = new ReviewDTO();
    reviewDTO.setId(1L);
    reviewDTO.setReviewerId(1L);
    reviewDTO.setRevieweeId(2L);
    reviewDTO.setRating(5);
    reviewDTO.setComment("Great service!");
    reviewDTO.setCreatedAt(LocalDateTime.now());

    User reviewer2 = new User();
    reviewer2.setId(2L);
    reviewer2.setUsername("reviewer2");

    User reviewee2 = new User();
    reviewee2.setId(1L);
    reviewee2.setUsername("reviewee2");

    Review review2 = new Review();
    review2.setId(2L);
    review2.setReviewer(reviewer2);
    review2.setReviewee(reviewee2);
    review2.setRating(4);
    review2.setComment("Good transaction");
    review2.setCreatedAt(LocalDateTime.now());

    ReviewDTO reviewDTO2 = new ReviewDTO();
    reviewDTO2.setId(2L);
    reviewDTO2.setReviewerId(2L);
    reviewDTO2.setRevieweeId(1L);
    reviewDTO2.setRating(4);
    reviewDTO2.setComment("Good transaction");
    reviewDTO2.setCreatedAt(LocalDateTime.now());

    reviewList = Arrays.asList(review, review2);
    reviewDTOList = Arrays.asList(reviewDTO, reviewDTO2);
  }

  @Test
  void getAllReviews() {
    // Arrange
    when(reviewRepository.findAll()).thenReturn(reviewList);
    when(reviewMapper.toDtoList(reviewList)).thenReturn(reviewDTOList);

    // Act
    List<ReviewDTO> result = reviewService.getAllReviews();

    // Assert
    assertEquals(2, result.size());
    assertEquals(reviewDTOList, result);
    verify(reviewRepository, times(1)).findAll();
    verify(reviewMapper, times(1)).toDtoList(reviewList);
  }

  @Test
  void getReview_ExistingId_ReturnsReview() {
    // Arrange
    Long reviewId = 1L;
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
    when(reviewMapper.toDto(review)).thenReturn(reviewDTO);

    // Act
    ReviewDTO result = reviewService.getReview(reviewId);

    // Assert
    assertNotNull(result);
    assertEquals(reviewDTO, result);
    verify(reviewRepository, times(1)).findById(reviewId);
    verify(reviewMapper, times(1)).toDto(review);
  }

  @Test
  void getReview_NonExistingId_ThrowsException() {
    // Arrange
    Long reviewId = 999L;
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ReviewNotFoundException.class,
        () -> {
          reviewService.getReview(reviewId);
        });
    verify(reviewRepository, times(1)).findById(reviewId);
    verify(reviewMapper, never()).toDto(any(Review.class));
  }

  @Test
  void getReviewsByReviewee() {
    // Arrange
    Long revieweeId = 2L;
    when(reviewRepository.findByRevieweeId(revieweeId)).thenReturn(Arrays.asList(review));
    when(reviewMapper.toDtoList(Arrays.asList(review))).thenReturn(Arrays.asList(reviewDTO));

    // Act
    List<ReviewDTO> result = reviewService.getReviewsByReviewee(revieweeId);

    // Assert
    assertEquals(1, result.size());
    assertEquals(reviewDTO, result.get(0));
    verify(reviewRepository, times(1)).findByRevieweeId(revieweeId);
    verify(reviewMapper, times(1)).toDtoList(Arrays.asList(review));
  }

  @Test
  void getReviewsByReviewer() {
    // Arrange
    Long reviewerId = 1L;
    when(reviewRepository.findByReviewerId(reviewerId)).thenReturn(Arrays.asList(review));
    when(reviewMapper.toDtoList(Arrays.asList(review))).thenReturn(Arrays.asList(reviewDTO));

    // Act
    List<ReviewDTO> result = reviewService.getReviewsByReviewer(reviewerId);

    // Assert
    assertEquals(1, result.size());
    assertEquals(reviewDTO, result.get(0));
    verify(reviewRepository, times(1)).findByReviewerId(reviewerId);
    verify(reviewMapper, times(1)).toDtoList(Arrays.asList(review));
  }

  @Test
  void createReview_ValidData_ReturnsCreatedReview() {
    // Arrange
    when(reviewMapper.toEntity(reviewDTO)).thenReturn(review);
    when(reviewRepository.save(any(Review.class))).thenReturn(review);
    when(reviewMapper.toDto(review)).thenReturn(reviewDTO);

    // Act
    ReviewDTO result = reviewService.createReview(reviewDTO);

    // Assert
    assertNotNull(result);
    assertEquals(reviewDTO, result);
    verify(reviewMapper, times(1)).toEntity(reviewDTO);
    verify(reviewRepository, times(1)).save(any(Review.class));
    verify(reviewMapper, times(1)).toDto(review);
  }

  @Test
  void createReview_InvalidRating_ThrowsException() {
    // Arrange
    ReviewDTO invalidReviewDTO = new ReviewDTO();
    invalidReviewDTO.setReviewerId(1L);
    invalidReviewDTO.setRevieweeId(2L);
    invalidReviewDTO.setRating(6); // Invalid rating (>5)
    invalidReviewDTO.setComment("Test");

    // Act & Assert
    assertThrows(
        ReviewValidationException.class,
        () -> {
          reviewService.createReview(invalidReviewDTO);
        });
    verify(reviewRepository, never()).save(any(Review.class));
  }

  @Test
  void createReview_SameReviewerAndReviewee_ThrowsException() {
    // Arrange
    ReviewDTO selfReviewDTO = new ReviewDTO();
    selfReviewDTO.setReviewerId(1L);
    selfReviewDTO.setRevieweeId(1L); // Same as reviewer
    selfReviewDTO.setRating(5);
    selfReviewDTO.setComment("Test");

    // Act & Assert
    assertThrows(
        ReviewValidationException.class,
        () -> {
          reviewService.createReview(selfReviewDTO);
        });
    verify(reviewRepository, never()).save(any(Review.class));
  }

  @Test
  void updateReview_ExistingId_ReturnsUpdatedReview() {
    // Arrange
    Long reviewId = 1L;
    ReviewDTO updateDTO = new ReviewDTO();
    updateDTO.setComment("Updated comment");
    updateDTO.setRating(4);

    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
    when(reviewRepository.save(any(Review.class))).thenReturn(review);
    when(reviewMapper.toDto(review)).thenReturn(reviewDTO);

    // Act
    ReviewDTO result = reviewService.updateReview(reviewId, updateDTO);

    // Assert
    assertNotNull(result);
    assertEquals(reviewDTO, result);
    verify(reviewRepository, times(1)).findById(reviewId);
    verify(reviewRepository, times(1)).save(review);
    verify(reviewMapper, times(1)).toDto(review);
  }

  @Test
  void updateReview_NonExistingId_ThrowsException() {
    // Arrange
    Long reviewId = 999L;
    ReviewDTO updateDTO = new ReviewDTO();
    updateDTO.setComment("Updated comment");
    updateDTO.setRating(4);

    when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ReviewNotFoundException.class,
        () -> {
          reviewService.updateReview(reviewId, updateDTO);
        });
    verify(reviewRepository, times(1)).findById(reviewId);
    verify(reviewRepository, never()).save(any(Review.class));
  }

  @Test
  void updateReview_InvalidRating_ThrowsException() {
    // Arrange
    Long reviewId = 1L;
    ReviewDTO updateDTO = new ReviewDTO();
    updateDTO.setRating(6); // Invalid rating (>5)

    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

    // Act & Assert
    assertThrows(
        ReviewValidationException.class,
        () -> {
          reviewService.updateReview(reviewId, updateDTO);
        });
    verify(reviewRepository, times(1)).findById(reviewId);
    verify(reviewRepository, never()).save(any(Review.class));
  }

  @Test
  void deleteReview_ExistingId_DeletesReview() {
    // Arrange
    Long reviewId = 1L;
    when(reviewRepository.existsById(reviewId)).thenReturn(true);
    doNothing().when(reviewRepository).deleteById(reviewId);

    // Act
    reviewService.deleteReview(reviewId);

    // Assert
    verify(reviewRepository, times(1)).existsById(reviewId);
    verify(reviewRepository, times(1)).deleteById(reviewId);
  }

  @Test
  void deleteReview_NonExistingId_ThrowsException() {
    // Arrange
    Long reviewId = 999L;
    when(reviewRepository.existsById(reviewId)).thenReturn(false);

    // Act & Assert
    assertThrows(
        ReviewNotFoundException.class,
        () -> {
          reviewService.deleteReview(reviewId);
        });
    verify(reviewRepository, times(1)).existsById(reviewId);
    verify(reviewRepository, never()).deleteById(reviewId);
  }
}
