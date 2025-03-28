package stud.ntnu.no.backend.review.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import stud.ntnu.no.backend.review.dto.ReviewDTO;
import stud.ntnu.no.backend.review.service.ReviewService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewDTO sampleReviewDTO;
    private List<ReviewDTO> reviewDTOList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup sample data
        sampleReviewDTO = new ReviewDTO();
        sampleReviewDTO.setId(1L);
        sampleReviewDTO.setReviewerId(101L);
        sampleReviewDTO.setRevieweeId(102L);
        sampleReviewDTO.setRating(4);
        sampleReviewDTO.setComment("Great service!");
        sampleReviewDTO.setCreatedAt(LocalDateTime.now());
        
        ReviewDTO reviewDTO2 = new ReviewDTO();
        reviewDTO2.setId(2L);
        reviewDTO2.setReviewerId(103L);
        reviewDTO2.setRevieweeId(102L);
        reviewDTO2.setRating(5);
        reviewDTO2.setComment("Excellent work!");
        
        reviewDTOList = Arrays.asList(sampleReviewDTO, reviewDTO2);
    }

    @Test
    void getAllReviews_ShouldReturnListOfReviews() {
        when(reviewService.getAllReviews()).thenReturn(reviewDTOList);
        
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviews();
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(reviewService, times(1)).getAllReviews();
    }

    @Test
    void getReview_WithValidId_ShouldReturnReview() {
        when(reviewService.getReview(1L)).thenReturn(sampleReviewDTO);
        
        ResponseEntity<ReviewDTO> response = reviewController.getReview(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Great service!", response.getBody().getComment());
        verify(reviewService, times(1)).getReview(1L);
    }

    @Test
    void getReviewsByReviewee_ShouldReturnReviewsList() {
        when(reviewService.getReviewsByReviewee(102L)).thenReturn(reviewDTOList);
        
        ResponseEntity<List<ReviewDTO>> response = reviewController.getReviewsByReviewee(102L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(reviewService, times(1)).getReviewsByReviewee(102L);
    }

    @Test
    void getReviewsByReviewer_ShouldReturnReviewsList() {
        when(reviewService.getReviewsByReviewer(101L)).thenReturn(List.of(sampleReviewDTO));
        
        ResponseEntity<List<ReviewDTO>> response = reviewController.getReviewsByReviewer(101L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(101L, response.getBody().get(0).getReviewerId());
        verify(reviewService, times(1)).getReviewsByReviewer(101L);
    }

    @Test
    void createReview_WithValidData_ShouldReturnCreatedReview() {
        when(reviewService.createReview(any(ReviewDTO.class))).thenReturn(sampleReviewDTO);
        
        ResponseEntity<ReviewDTO> response = reviewController.createReview(new ReviewDTO());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(reviewService, times(1)).createReview(any(ReviewDTO.class));
    }

    @Test
    void updateReview_WithValidData_ShouldReturnUpdatedReview() {
        when(reviewService.updateReview(eq(1L), any(ReviewDTO.class))).thenReturn(sampleReviewDTO);
        
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(1L, new ReviewDTO());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(reviewService, times(1)).updateReview(eq(1L), any(ReviewDTO.class));
    }

    @Test
    void deleteReview_ShouldReturnNoContent() {
        doNothing().when(reviewService).deleteReview(1L);
        
        ResponseEntity<Void> response = reviewController.deleteReview(1L);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reviewService, times(1)).deleteReview(1L);
    }
}