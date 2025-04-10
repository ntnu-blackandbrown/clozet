package stud.ntnu.no.backend.transaction.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for updating a transaction.
 * <p>
 * This class holds information required to update a transaction, including amount, status, and
 * payment method.
 */
public class UpdateTransactionDTO {

  private BigDecimal amount;
  private String status;
  private String paymentMethod;

  // Default constructor
  public UpdateTransactionDTO() {
  }

  /**
   * Returns the amount of the transaction.
   *
   * @return the amount
   */
  public BigDecimal getAmount() {
    return amount;
  }

  /**
   * Sets the amount of the transaction.
   *
   * @param amount the amount to set
   */
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  /**
   * Returns the status of the transaction.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the status of the transaction.
   *
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Returns the payment method of the transaction.
   *
   * @return the payment method
   */
  public String getPaymentMethod() {
    return paymentMethod;
  }

  /**
   * Sets the payment method of the transaction.
   *
   * @param paymentMethod the payment method to set
   */
  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }
}