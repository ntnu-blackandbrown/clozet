package stud.ntnu.no.backend.review.service;

import stud.ntnu.no.backend.review.dto.ReviewDTO;

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