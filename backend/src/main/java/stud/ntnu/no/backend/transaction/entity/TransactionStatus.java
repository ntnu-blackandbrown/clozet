package stud.ntnu.no.backend.transaction.entity;

/**
 * Enumeration of possible transaction statuses.
 * <p>
 * This enum defines the various states a transaction can be in, such as PENDING, COMPLETED, FAILED,
 * and CANCELLED.
 */
public enum TransactionStatus {
  PENDING,
  COMPLETED,
  FAILED,
  CANCELLED
}