package stud.ntnu.no.backend.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import stud.ntnu.no.backend.transaction.entity.Transaction;
import stud.ntnu.no.backend.user.entity.User;

/**
 * Entity representing a review.
 * <p>
 * This class is mapped to the "REVIEWS" table in the database and holds information about a review,
 * including its ID, comment, rating, creation time, reviewee, reviewer, and associated
 * transaction.
 */
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

  /**
   * Returns the ID of the review.
   *
   * @return the review ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the review.
   *
   * @param id the review ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns the comment of the review.
   *
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the comment of the review.
   *
   * @param comment the comment to set
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Returns the rating of the review.
   *
   * @return the rating
   */
  public int getRating() {
    return rating;
  }

  /**
   * Sets the rating of the review.
   *
   * @param rating the rating to set
   */
  public void setRating(int rating) {
    this.rating = rating;
  }

  /**
   * Returns the creation time of the review.
   *
   * @return the creation time
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the creation time of the review.
   *
   * @param createdAt the creation time to set
   */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * Returns the reviewee.
   *
   * @return the reviewee
   */
  public User getReviewee() {
    return reviewee;
  }

  /**
   * Sets the reviewee.
   *
   * @param reviewee the reviewee to set
   */
  public void setReviewee(User reviewee) {
    this.reviewee = reviewee;
  }

  /**
   * Returns the reviewer.
   *
   * @return the reviewer
   */
  public User getReviewer() {
    return reviewer;
  }

  /**
   * Sets the reviewer.
   *
   * @param reviewer the reviewer to set
   */
  public void setReviewer(User reviewer) {
    this.reviewer = reviewer;
  }

  /**
   * Returns the associated transaction.
   *
   * @return the transaction
   */
  public Transaction getTransaction() {
    return transaction;
  }

  /**
   * Sets the associated transaction.
   *
   * @param transaction the transaction to set
   */
  public void setTransaction(Transaction transaction) {
    this.transaction = transaction;
  }
}