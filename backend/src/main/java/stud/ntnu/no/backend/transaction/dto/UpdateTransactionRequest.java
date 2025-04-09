package stud.ntnu.no.backend.transaction.dto;

/**
 * Request object for updating a transaction.
 *
 * <p>This class holds information required to update a transaction, including status and payment
 * method.
 */
public class UpdateTransactionRequest {

  private String status;
  private String paymentMethod;

  // Constructors
  public UpdateTransactionRequest() {}

  public UpdateTransactionRequest(String status, String paymentMethod) {
    this.status = status;
    this.paymentMethod = paymentMethod;
  }

  // Getters and Setters

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
