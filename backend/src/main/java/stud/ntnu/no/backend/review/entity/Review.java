package stud.ntnu.no.backend.review.entity;

import jakarta.persistence.*;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.transaction.entity.Transaction;

import java.time.LocalDateTime;

@Entity
@Table(name = "REVIEWS")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String comment;
    
    private int rating;
    
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "REVIEWEE_ID")
    private User reviewee;
    
    @ManyToOne
    @JoinColumn(name = "REVIEWER_ID")
    private User reviewer;
    
    @ManyToOne
    @JoinColumn(name = "TRANSACTION_ID")
    private Transaction transaction;
    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getReviewee() {
        return reviewee;
    }

    public void setReviewee(User reviewee) {
        this.reviewee = reviewee;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}