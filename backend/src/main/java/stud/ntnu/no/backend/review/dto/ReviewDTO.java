package stud.ntnu.no.backend.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for a review.
 *
 * <p>This class holds information about a review, including its ID, comment, rating, creation time,
 * reviewee and reviewer details, and transaction ID.
 */
public class ReviewDTO {

  private Long id;

  private String comment;

  @NotNull(message = "Rating is required")
  @Min(value = 1, message = "Rating must be at least 1")
  @Max(value = 5, message = "Rating cannot be more than 5")
  private Integer rating;

  private LocalDateTime createdAt;

  private Long revieweeId;
  private String revieweeUsername;

  private Long reviewerId;
  private String reviewerUsername;

  private Long transactionId;

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
  public Integer getRating() {
    return rating;
  }

  /**
   * Sets the rating of the review.
   *
   * @param rating the rating to set
   */
  public void setRating(Integer rating) {
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
   * Returns the ID of the reviewee.
   *
   * @return the reviewee ID
   */
  public Long getRevieweeId() {
    return revieweeId;
  }

  /**
   * Sets the ID of the reviewee.
   *
   * @param revieweeId the reviewee ID to set
   */
  public void setRevieweeId(Long revieweeId) {
    this.revieweeId = revieweeId;
  }

  /**
   * Returns the username of the reviewee.
   *
   * @return the reviewee username
   */
  public String getRevieweeUsername() {
    return revieweeUsername;
  }

  /**
   * Sets the username of the reviewee.
   *
   * @param revieweeUsername the reviewee username to set
   */
  public void setRevieweeUsername(String revieweeUsername) {
    this.revieweeUsername = revieweeUsername;
  }

  /**
   * Returns the ID of the reviewer.
   *
   * @return the reviewer ID
   */
  public Long getReviewerId() {
    return reviewerId;
  }

  /**
   * Sets the ID of the reviewer.
   *
   * @param reviewerId the reviewer ID to set
   */
  public void setReviewerId(Long reviewerId) {
    this.reviewerId = reviewerId;
  }

  /**
   * Returns the username of the reviewer.
   *
   * @return the reviewer username
   */
  public String getReviewerUsername() {
    return reviewerUsername;
  }

  /**
   * Sets the username of the reviewer.
   *
   * @param reviewerUsername the reviewer username to set
   */
  public void setReviewerUsername(String reviewerUsername) {
    this.reviewerUsername = reviewerUsername;
  }

  /**
   * Returns the ID of the transaction.
   *
   * @return the transaction ID
   */
  public Long getTransactionId() {
    return transactionId;
  }

  /**
   * Sets the ID of the transaction.
   *
   * @param transactionId the transaction ID to set
   */
  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }
}
