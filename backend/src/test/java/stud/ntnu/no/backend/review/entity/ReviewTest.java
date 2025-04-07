package stud.ntnu.no.backend.review.entity;

import org.junit.jupiter.api.Test;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.transaction.entity.Transaction;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void testDefaultConstructor() {
        Review review = new Review();
        
        assertNull(review.getId());
        assertNull(review.getComment());
        assertEquals(0, review.getRating());
        assertNull(review.getCreatedAt());
        assertNull(review.getReviewee());
        assertNull(review.getReviewer());
        assertNull(review.getTransaction());
    }
    
    @Test
    void testGettersAndSetters() {
        Review review = new Review();
        
        Long id = 1L;
        String comment = "Excellent service!";
        int rating = 5;
        LocalDateTime createdAt = LocalDateTime.now();
        User reviewee = new User();
        User reviewer = new User();
        Transaction transaction = new Transaction();
        
        review.setId(id);
        review.setComment(comment);
        review.setRating(rating);
        review.setCreatedAt(createdAt);
        review.setReviewee(reviewee);
        review.setReviewer(reviewer);
        review.setTransaction(transaction);
        
        assertEquals(id, review.getId());
        assertEquals(comment, review.getComment());
        assertEquals(rating, review.getRating());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(reviewee, review.getReviewee());
        assertEquals(reviewer, review.getReviewer());
        assertEquals(transaction, review.getTransaction());
    }
    
    @Test
    void testRatingBounds() {
        Review review = new Review();
        
        // Test minimum rating
        review.setRating(1);
        assertEquals(1, review.getRating());
        
        // Test maximum rating
        review.setRating(5);
        assertEquals(5, review.getRating());
        
        // Test middle rating
        review.setRating(3);
        assertEquals(3, review.getRating());
    }
    
    @Test
    void testUserRelationships() {
        Review review = new Review();
        
        User reviewee = new User();
        reviewee.setId(1L);
        reviewee.setUsername("seller");
        
        User reviewer = new User();
        reviewer.setId(2L);
        reviewer.setUsername("buyer");
        
        review.setReviewee(reviewee);
        review.setReviewer(reviewer);
        
        assertEquals(reviewee, review.getReviewee());
        assertEquals("seller", review.getReviewee().getUsername());
        
        assertEquals(reviewer, review.getReviewer());
        assertEquals("buyer", review.getReviewer().getUsername());
        
        // Verify the relationships are distinct
        assertNotEquals(review.getReviewee(), review.getReviewer());
    }
    
    @Test
    void testTransactionRelationship() {
        Review review = new Review();
        
        Transaction transaction = new Transaction();
        transaction.setId(123L);
        
        review.setTransaction(transaction);
        
        assertEquals(transaction, review.getTransaction());
        assertEquals(123L, review.getTransaction().getId());
    }
} 