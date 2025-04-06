package stud.ntnu.no.backend.review.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stud.ntnu.no.backend.review.dto.ReviewDTO;
import stud.ntnu.no.backend.review.service.ReviewService;

import java.util.List;

/**
 * REST controller for managing reviews.
 * <p>
 * This controller provides endpoints for CRUD operations on reviews.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    /**
     * Constructs a new ReviewController with the specified service.
     *
     * @param reviewService the ReviewService
     */
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }
    
    /**
     * Retrieves all reviews.
     *
     * @return a list of ReviewDTOs
     */
    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
    
    /**
     * Retrieves a review by its ID.
     *
     * @param id the ID of the review
     * @return the ReviewDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReview(id));
    }
    
    /**
     * Retrieves reviews by the reviewee's ID.
     *
     * @param revieweeId the ID of the reviewee
     * @return a list of ReviewDTOs
     */
    @GetMapping("/reviewee/{revieweeId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByReviewee(@PathVariable Long revieweeId) {
        return ResponseEntity.ok(reviewService.getReviewsByReviewee(revieweeId));
    }
    
    /**
     * Retrieves reviews by the reviewer's ID.
     *
     * @param reviewerId the ID of the reviewer
     * @return a list of ReviewDTOs
     */
    @GetMapping("/reviewer/{reviewerId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByReviewer(@PathVariable Long reviewerId) {
        return ResponseEntity.ok(reviewService.getReviewsByReviewer(reviewerId));
    }
    
    /**
     * Creates a new review.
     *
     * @param reviewDTO the ReviewDTO
     * @return the created ReviewDTO
     */
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
    }
    
    /**
     * Updates an existing review.
     *
     * @param id the ID of the review to update
     * @param reviewDTO the ReviewDTO with updated information
     * @return the updated ReviewDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.updateReview(id, reviewDTO));
    }
    
    /**
     * Deletes a review by its ID.
     *
     * @param id the ID of the review to delete
     * @return a ResponseEntity with status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}