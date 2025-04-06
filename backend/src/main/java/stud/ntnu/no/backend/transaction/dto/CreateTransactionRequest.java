package stud.ntnu.no.backend.transaction.dto;

/**
 * Request object for creating a transaction.
 * <p>
 * This class holds information required to create a new transaction,
 * including item ID, buyer ID, seller ID, amount, status, and payment method.
 */
public class CreateTransactionRequest {
    private Long itemId;
    private String buyerId;
    private String sellerId;
    private double amount;
    private String status;
    private String paymentMethod;

    // Constructors
    public CreateTransactionRequest() {
    }

    public CreateTransactionRequest(Long itemId, String buyerId, String sellerId, double amount,
                              String status, String paymentMethod) {
        this.itemId = itemId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    // Getters and setters
    /**
     * Returns the item ID associated with the transaction.
     *
     * @return the item ID
     */
    public Long getItemId() {
        return itemId;
    }

    /**
     * Sets the item ID associated with the transaction.
     *
     * @param itemId the item ID to set
     */
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    /**
     * Returns the buyer ID of the transaction.
     *
     * @return the buyer ID
     */
    public String getBuyerId() {
        return buyerId;
    }

    /**
     * Sets the buyer ID of the transaction.
     *
     * @param buyerId the buyer ID to set
     */
    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    /**
     * Returns the seller ID of the transaction.
     *
     * @return the seller ID
     */
    public String getSellerId() {
        return sellerId;
    }

    /**
     * Sets the seller ID of the transaction.
     *
     * @param sellerId the seller ID to set
     */
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * Returns the amount of the transaction.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the transaction.
     *
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
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