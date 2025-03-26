package stud.ntnu.no.backend.Review.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stud.ntnu.no.backend.Review.DTOs.ReviewDTO;
import stud.ntnu.no.backend.Review.Entity.Review;
import stud.ntnu.no.backend.Review.Exceptions.ReviewNotFoundException;
import stud.ntnu.no.backend.Review.Exceptions.ReviewValidationException;
import stud.ntnu.no.backend.Review.Mapper.ReviewMapper;
import stud.ntnu.no.backend.Review.Repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<ReviewDTO> getAllReviews() {
        return reviewMapper.toDtoList(reviewRepository.findAll());
    }

    @Override
    public ReviewDTO getReview(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
        return reviewMapper.toDto(review);
    }

    @Override
    public List<ReviewDTO> getReviewsByReviewee(Long revieweeId) {
        return reviewMapper.toDtoList(reviewRepository.findByRevieweeId(revieweeId));
    }

    @Override
    public List<ReviewDTO> getReviewsByReviewer(Long reviewerId) {
        return reviewMapper.toDtoList(reviewRepository.findByReviewerId(reviewerId));
    }

    @Override
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        validateReview(reviewDTO);
        Review review = reviewMapper.toEntity(reviewDTO);
        review.setCreatedAt(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
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
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

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
    
    private void validateUpdateReview(ReviewDTO reviewDTO) {
        if (reviewDTO.getRating() != null && (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5)) {
            throw new ReviewValidationException("Rating must be between 1 and 5");
        }
    }
}