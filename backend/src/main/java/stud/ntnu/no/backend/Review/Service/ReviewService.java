package stud.ntnu.no.backend.Review.Service;

import stud.ntnu.no.backend.Review.DTOs.ReviewDTO;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllReviews();
    ReviewDTO getReview(Long id);
    List<ReviewDTO> getReviewsByReviewee(Long revieweeId);
    List<ReviewDTO> getReviewsByReviewer(Long reviewerId);
    ReviewDTO createReview(ReviewDTO reviewDTO);
    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);
    void deleteReview(Long id);
}